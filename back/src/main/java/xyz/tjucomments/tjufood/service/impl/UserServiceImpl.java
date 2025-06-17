package xyz.tjucomments.tjufood.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.tjucomments.tjufood.dto.*;
import xyz.tjucomments.tjufood.entity.User;
import xyz.tjucomments.tjufood.mapper.UserMapper;
import xyz.tjucomments.tjufood.service.IUserService;
import xyz.tjucomments.tjufood.utils.constants.IdPrefixConstants;
import xyz.tjucomments.tjufood.utils.constants.RedisConstants;
import xyz.tjucomments.tjufood.utils.id.RedisIdWorker;
import xyz.tjucomments.tjufood.utils.security.DataMaskingUtils;
import xyz.tjucomments.tjufood.utils.security.RateLimitUtils;
import xyz.tjucomments.tjufood.utils.validation.RegexUtils;
import cn.hutool.core.util.StrUtil; // ****** 新增的导入语句 ******
import org.springframework.transaction.annotation.Transactional;
import xyz.tjucomments.tjufood.dto.UserDTO;
import xyz.tjucomments.tjufood.entity.CreditRecord; // 假设您已创建积分记录实体
import xyz.tjucomments.tjufood.interceptor.UserHolder;
import xyz.tjucomments.tjufood.service.ICreditRecordService; // 假设您已创建积分记录服务

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * @author Gemini
 * @description 用户服务实现类 (已重构)
 * @create 2025-06-16 14:15
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private JavaMailSender mailSender;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private RedisIdWorker redisIdWorker;

    @Resource
    private RateLimitUtils rateLimitUtils;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Result sendCode(SendCodeDTO sendCodeDTO) {
        // ... (此方法逻辑保持不变)
        String captchaKey = RedisConstants.CAPTCHA_CODE_KEY + sendCodeDTO.getCaptchaKey();
        String cachedCaptcha = stringRedisTemplate.opsForValue().get(captchaKey);
        stringRedisTemplate.delete(captchaKey);
        if (cachedCaptcha == null || !cachedCaptcha.equalsIgnoreCase(sendCodeDTO.getCaptchaCode())) {
            return Result.fail("图形验证码错误！");
        }

        String email = sendCodeDTO.getEmail();
        Integer type = sendCodeDTO.getType();
        if (RegexUtils.isEmailInvalid(email)) {
            return Result.fail("邮箱格式不正确！");
        }

        // 防重放限制：检查验证码发送频率
        if (!rateLimitUtils.checkSmsRateLimit(email)) {
            long remainingTime = rateLimitUtils.getRemainingCooldown(email, "sms_limit");
            return Result.fail("验证码发送过于频繁，请 " + remainingTime + " 秒后再试");
        }

        User user = getOne(new QueryWrapper<User>().eq("email", email));

        if (type == 0 && user != null) return Result.fail("该邮箱已被注册！");
        if (type == 2 && user == null) return Result.fail("该邮箱未注册！");

        String code = RandomUtil.randomNumbers(6);
        String emailCodeKey = RedisConstants.LOGIN_CODE_KEY + type + ":" + email;
        stringRedisTemplate.opsForValue().set(emailCodeKey, code, RedisConstants.LOGIN_CODE_TTL, TimeUnit.MINUTES);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("TjuFood 验证码");
        message.setText("您的验证码是：" + code + "，有效期5分钟，请勿泄露。");
        mailSender.send(message);

        return Result.ok("验证码发送成功");
    }

    @Override
    @Transactional
    public Result register(RegisterFormDTO registerForm) {
        // ... (前置校验逻辑不变)
        if (!registerForm.getPassword().equals(registerForm.getConfirmPassword())) {
            return Result.fail("两次输入的密码不一致！");
        }
        if (RegexUtils.isCodeInvalid(registerForm.getCode())) {
            return Result.fail("验证码格式错误！");
        }
        long count = count(new QueryWrapper<User>().eq("email", registerForm.getEmail()));
        if (count > 0) {
            return Result.fail("该邮箱已被注册！");
        }
        String key = RedisConstants.LOGIN_CODE_KEY + "0:" + registerForm.getEmail();
        String cachedCode = stringRedisTemplate.opsForValue().get(key);
        if (cachedCode == null || !cachedCode.equals(registerForm.getCode())) {
            return Result.fail("邮箱验证码错误！");
        }


        // 【高优先级修复】为新用户生成并设置ID
        long userId = redisIdWorker.nextId(IdPrefixConstants.USER_ID_PREFIX);
        User user = new User();
        user.setId(userId);
        user.setEmail(registerForm.getEmail());
        user.setPassword(passwordEncoder.encode(registerForm.getPassword()));
        // 如果昵称为空，则生成一个默认昵称
        user.setNickname(StrUtil.isNotBlank(registerForm.getNickname()) ?
                registerForm.getNickname() :
                IdPrefixConstants.USER_ID_PREFIX + userId);
        user.setGender(registerForm.getGender());
        user.setCampus(registerForm.getCampus());

        try {
            save(user);
        } catch (Exception e) {
            log.error("用户注册时数据库操作失败！", e);
            return Result.fail("注册失败，请稍后重试");
        }

        stringRedisTemplate.delete(key);
        return Result.ok("注册成功！");
    }

    @Override
    public Result login(LoginFormDTO loginForm) {
        // ... (此方法逻辑保持不变)
        User user;
        String account = loginForm.getAccount();

        if (account.contains("@")) {
            user = getOne(new QueryWrapper<User>().eq("email", account));
        } else {
            try {
                user = getById(Long.parseLong(account));
            } catch (NumberFormatException e) {
                return Result.fail("账号格式错误！");
            }
        }

        if (user == null || !passwordEncoder.matches(loginForm.getPassword(), user.getPassword())) {
            return Result.fail("账号或密码不对");
        }

        String token = UUID.randomUUID().toString(true);
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        try {
            String userDTOJson = objectMapper.writeValueAsString(userDTO);
            String tokenKey = RedisConstants.LOGIN_USER_KEY + token;
            stringRedisTemplate.opsForValue().set(tokenKey, userDTOJson, RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            log.error("用户登录序列化DTO失败", e);
            return Result.fail("登录失败，系统内部错误");
        }
        return Result.ok(token);
    }

    @Override
    public Result resetPassword(PasswordResetDTO resetForm) {
        // ... (此方法逻辑保持不变)
        String key = RedisConstants.LOGIN_CODE_KEY + "2:" + resetForm.getEmail();
        String cachedCode = stringRedisTemplate.opsForValue().get(key);
        if (cachedCode == null || !cachedCode.equals(resetForm.getCode())) {
            return Result.fail("验证码错误！");
        }
        String newPassword = passwordEncoder.encode(resetForm.getNewPassword());
        baseMapper.updatePasswordByEmail(resetForm.getEmail(), newPassword);
        stringRedisTemplate.delete(key);
        return Result.ok("密码重置成功！");
    }

    @Override
    public Result logout(String token) {
        // ... (此方法逻辑保持不变)
        String tokenKey = RedisConstants.LOGIN_USER_KEY + token;
        stringRedisTemplate.delete(tokenKey);
        return Result.ok();
    }
    // 【新增】注入积分记录服务
    @Resource
    private ICreditRecordService creditRecordService;

    /**
     * 获取当前登录用户的详细信息
     */
    @Override
    public Result queryMe() {
        UserDTO userDTO = UserHolder.getUser();
        if (userDTO == null) return Result.fail("用户未登录");
        User user = getById(userDTO.getId());
        return Result.ok(user);
    }

    @Override
    public Result queryUserById(Long userId) {
        User user = getById(userId);
        if (user == null) return Result.fail("用户不存在！");

        // 数据脱敏处理
        user.setPassword(null);
        if (user.getPhone() != null) {
            user.setPhone(DataMaskingUtils.maskPhone(user.getPhone()));
        }
        if (user.getEmail() != null) {
            user.setEmail(DataMaskingUtils.maskEmail(user.getEmail()));
        }

        return Result.ok(user);
    }

    @Override
    @Transactional
    public Result updateUserProfile(UserDTO userDTO) {
        Long userId = UserHolder.getUser().getId();
        User user = new User();
        user.setId(userId);

        // 【修正】使用正确的setter方法名 setNickname
        user.setNickname(userDTO.getNickName());
        user.setIcon(userDTO.getIcon());

        boolean isSuccess = updateById(user);
        return isSuccess ? Result.ok() : Result.fail("更新失败！");
    }

    @Override
    @Transactional
    public Result sign() {
        Long userId = UserHolder.getUser().getId();
        LocalDateTime now = LocalDateTime.now();
        String dateSuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String key = RedisConstants.USER_SIGN_KEY + userId + dateSuffix;
        int dayOfMonth = now.getDayOfMonth();

        if (Boolean.TRUE.equals(stringRedisTemplate.opsForValue().getBit(key, dayOfMonth - 1))) {
            return Result.fail("您今天已经签过到了！");
        }

        stringRedisTemplate.opsForValue().setBit(key, dayOfMonth - 1, true);

        update().setSql("credits = credits + 5").eq("id", userId).update();

        CreditRecord record = new CreditRecord();
        record.setId(redisIdWorker.nextId("credit"));
        record.setUserId(userId);
        record.setCredits(5);
        record.setActionType("USER_SIGN_IN");
        record.setDescription("每日签到奖励");
        creditRecordService.save(record);

        return Result.ok("签到成功！积分+5");
    }
}
