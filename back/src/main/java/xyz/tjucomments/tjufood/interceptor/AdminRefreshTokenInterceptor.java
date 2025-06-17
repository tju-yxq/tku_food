package xyz.tjucomments.tjufood.interceptor;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import xyz.tjucomments.tjufood.dto.AdminDTO;
import xyz.tjucomments.tjufood.utils.constants.RedisConstants;

import java.util.concurrent.TimeUnit;


@Component
public class AdminRefreshTokenInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public AdminRefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String header = request.getHeader("authorization");
        if (StrUtil.isBlank(header)) {
            return true;
        }

        // --- 新增逻辑：处理 "Bearer " 前缀 ---
        String token = header;
        String bearerPrefix = "Bearer ";
        if (header.startsWith(bearerPrefix)) {
            // 如果请求头以 "Bearer " 开头，则截取后面的纯Token部分
            token = header.substring(bearerPrefix.length());
        }

        String key = RedisConstants.LOGIN_ADMIN_KEY + token;
        String adminJson = stringRedisTemplate.opsForValue().get(key);

        if (StrUtil.isBlank(adminJson)) {
            return true;
        }

        AdminDTO adminDTO = objectMapper.readValue(adminJson, AdminDTO.class);
        AdminHolder.saveAdmin(adminDTO);
        stringRedisTemplate.expire(key, RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        AdminHolder.removeAdmin();
    }
}