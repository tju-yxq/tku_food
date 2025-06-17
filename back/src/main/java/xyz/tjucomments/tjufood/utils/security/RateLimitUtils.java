package xyz.tjucomments.tjufood.utils.security;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 防重放限制工具类
 * 符合阿里巴巴Java开发手册安全规约第7条：实现正确的防重放限制
 */
@Slf4j
@Component
public class RateLimitUtils {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // Redis key前缀
    private static final String RATE_LIMIT_PREFIX = "rate_limit:";
    private static final String SMS_LIMIT_PREFIX = "sms_limit:";
    private static final String API_LIMIT_PREFIX = "api_limit:";

    /**
     * 检查验证码发送频率限制
     * @param email 邮箱
     * @return true=允许发送, false=频率过高
     */
    public boolean checkSmsRateLimit(String email) {
        String key = SMS_LIMIT_PREFIX + email;
        String count = stringRedisTemplate.opsForValue().get(key);
        
        if (count == null) {
            // 第一次发送，设置计数器
            stringRedisTemplate.opsForValue().set(key, "1", 1, TimeUnit.MINUTES);
            return true;
        }
        
        int currentCount = Integer.parseInt(count);
        if (currentCount >= 3) {
            log.warn("邮箱 {} 验证码发送频率过高，当前计数: {}", email, currentCount);
            return false;
        }
        
        // 增加计数
        stringRedisTemplate.opsForValue().increment(key);
        return true;
    }

    /**
     * 检查API调用频率限制
     * @param userId 用户ID
     * @param apiPath API路径
     * @param maxRequests 最大请求次数
     * @param timeWindow 时间窗口（秒）
     * @return true=允许调用, false=频率过高
     */
    public boolean checkApiRateLimit(Long userId, String apiPath, int maxRequests, int timeWindow) {
        String key = API_LIMIT_PREFIX + userId + ":" + apiPath;
        String count = stringRedisTemplate.opsForValue().get(key);
        
        if (count == null) {
            stringRedisTemplate.opsForValue().set(key, "1", timeWindow, TimeUnit.SECONDS);
            return true;
        }
        
        int currentCount = Integer.parseInt(count);
        if (currentCount >= maxRequests) {
            log.warn("用户 {} 对API {} 调用频率过高，当前计数: {}", userId, apiPath, currentCount);
            return false;
        }
        
        stringRedisTemplate.opsForValue().increment(key);
        return true;
    }

    /**
     * 检查通用操作频率限制
     * @param identifier 标识符（如用户ID、IP等）
     * @param operation 操作类型
     * @param maxOperations 最大操作次数
     * @param timeWindow 时间窗口（秒）
     * @return true=允许操作, false=频率过高
     */
    public boolean checkOperationRateLimit(String identifier, String operation, int maxOperations, int timeWindow) {
        String key = RATE_LIMIT_PREFIX + operation + ":" + identifier;
        String count = stringRedisTemplate.opsForValue().get(key);
        
        if (count == null) {
            stringRedisTemplate.opsForValue().set(key, "1", timeWindow, TimeUnit.SECONDS);
            return true;
        }
        
        int currentCount = Integer.parseInt(count);
        if (currentCount >= maxOperations) {
            log.warn("标识符 {} 对操作 {} 频率过高，当前计数: {}", identifier, operation, currentCount);
            return false;
        }
        
        stringRedisTemplate.opsForValue().increment(key);
        return true;
    }

    /**
     * 获取剩余冷却时间（秒）
     */
    public long getRemainingCooldown(String identifier, String operation) {
        String key = RATE_LIMIT_PREFIX + operation + ":" + identifier;
        return stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 清除频率限制记录（管理员操作）
     */
    public void clearRateLimit(String identifier, String operation) {
        String key = RATE_LIMIT_PREFIX + operation + ":" + identifier;
        stringRedisTemplate.delete(key);
        log.info("已清除 {} 的 {} 操作频率限制", identifier, operation);
    }
}
