package xyz.tjucomments.tjufood.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.tjucomments.tjufood.dto.*;
import xyz.tjucomments.tjufood.entity.Admin;
import xyz.tjucomments.tjufood.entity.AdminRole;
import xyz.tjucomments.tjufood.entity.Role; // ****** 新增的导入语句 ******
import xyz.tjucomments.tjufood.mapper.AdminMapper;
import xyz.tjucomments.tjufood.mapper.AdminRoleMapper;
import xyz.tjucomments.tjufood.mapper.RoleMapper;
import xyz.tjucomments.tjufood.service.IAdminService;
import xyz.tjucomments.tjufood.utils.constants.RedisConstants;


import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {

    private static final String DEFAULT_INITIAL_PASSWORD = "123456";
    private static final String SYSTEM_ADMIN_ROLE_NAME = "系统管理员";

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RoleMapper roleMapper;

//    @Resource
//    private AdminRoleMapper adminRoleMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public AdminInfoVO login(AdminLoginDTO loginForm) {
        Admin admin = getOne(new QueryWrapper<Admin>().eq("username", loginForm.getUsername()));

        // 统一的错误处理，避免时序攻击
        boolean isValidLogin = false;
        if (admin != null) {
            // 即使用户不存在，也执行密码验证以保持相同的处理时间
            isValidLogin = passwordEncoder.matches(loginForm.getPassword(), admin.getPassword());
        } else {
            // 用户不存在时，也执行一次密码编码操作以保持相同的处理时间
            passwordEncoder.matches(loginForm.getPassword(), "$2a$10$dummyHashToPreventTimingAttack");
        }

        if (!isValidLogin || admin == null) {
            throw new RuntimeException("账号或密码不对");
        }

        if (admin.getStatus() != 0) {
            throw new RuntimeException("该管理员账号已被禁用！");
        }
        String token = UUID.randomUUID().toString(true);
        AdminDTO adminDTO = BeanUtil.copyProperties(admin, AdminDTO.class);
        try {
            String adminDTOJson = objectMapper.writeValueAsString(adminDTO);
            String tokenKey = RedisConstants.LOGIN_ADMIN_KEY + token;
            stringRedisTemplate.opsForValue().set(tokenKey, adminDTOJson, RedisConstants.LOGIN_ADMIN_TTL, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            log.error("管理员登录序列化DTO失败", e);
            throw new RuntimeException("登录失败，系统内部错误");
        }
        AdminInfoVO adminInfoVO = new AdminInfoVO();
        adminInfoVO.setToken(token);
        adminInfoVO.setId(admin.getId());
        adminInfoVO.setUsername(admin.getUsername());
        adminInfoVO.setName(admin.getName());
        adminInfoVO.setAvatar("");
        adminInfoVO.setRoles(getRolesByUsername(admin.getUsername()));
        return adminInfoVO;
    }

    @Override
    public List<String> getRolesByUsername(String username) {
        Admin admin = getOne(new QueryWrapper<Admin>().eq("username", username));
        if (admin == null) {
            return List.of();
        }
        return baseMapper.findRoleNamesByAdminId(admin.getId());
    }

    @Override
    public Result logout(String token) {
        String tokenKey = RedisConstants.LOGIN_ADMIN_KEY + token;
        stringRedisTemplate.delete(tokenKey);
        return Result.ok();
    }

    @Override
    @Transactional
    public Result createAdmin(AdminCreateDTO createDTO) {
        Role roleToAssign = roleMapper.selectById(createDTO.getRoleId());
        if (roleToAssign == null) {
            return Result.fail("指定的角色不存在！");
        }
        if (SYSTEM_ADMIN_ROLE_NAME.equals(roleToAssign.getName())) {
            return Result.fail("不允许通过此接口创建系统管理员！");
        }
        long count = this.count(new QueryWrapper<Admin>().eq("username", createDTO.getUsername()));
        if (count > 0) {
            return Result.fail("该管理员用户名已存在！");
        }
        Admin admin = new Admin();
        admin.setUsername(createDTO.getUsername());
        admin.setName(createDTO.getName());
        admin.setPassword(passwordEncoder.encode(DEFAULT_INITIAL_PASSWORD));
        admin.setRoleId(createDTO.getRoleId());
        this.save(admin);

//        AdminRole adminRole = new AdminRole();
//        adminRole.setAdminId(admin.getId());
//        adminRole.setRoleId(createDTO.getRoleId());
//        adminRoleMapper.insert(adminRole);

        return Result.ok(admin.getId());
    }
}