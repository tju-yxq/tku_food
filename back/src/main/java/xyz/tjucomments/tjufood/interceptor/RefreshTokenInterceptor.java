package xyz.tjucomments.tjufood.interceptor;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import xyz.tjucomments.tjufood.dto.UserDTO;
import xyz.tjucomments.tjufood.utils.constants.RedisConstants;

import java.util.concurrent.TimeUnit;

@Component
public class RefreshTokenInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
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
            token = header.substring(bearerPrefix.length());
        }

        String key = RedisConstants.LOGIN_USER_KEY + token;
        String userJson = stringRedisTemplate.opsForValue().get(key);

        if (StrUtil.isBlank(userJson)) {
            return true;
        }

        UserDTO userDTO = objectMapper.readValue(userJson, UserDTO.class);
        UserHolder.saveUser(userDTO);
        stringRedisTemplate.expire(key, RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}