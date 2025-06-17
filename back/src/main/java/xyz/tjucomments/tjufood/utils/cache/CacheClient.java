package xyz.tjucomments.tjufood.utils.cache;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import xyz.tjucomments.tjufood.utils.constants.RedisConstants;


import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author Gemini
 * @description 封装的Redis缓存工具类，提供了缓存穿透和缓存击穿的解决方案
 * @create 2025-06-16 14:15
 */
@Slf4j
@Component
public class CacheClient {

    private final StringRedisTemplate stringRedisTemplate;
    private final ExecutorService cacheRebuildExecutor;

    public CacheClient(StringRedisTemplate stringRedisTemplate,
                       @Qualifier("cacheRebuildExecutor") ExecutorService cacheRebuildExecutor) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.cacheRebuildExecutor = cacheRebuildExecutor;
    }

    /**
     * 将任意Java对象序列化为JSON并存储到Redis，并设置TTL。
     * @param key   键
     * @param value 值
     * @param time  时间
     * @param unit  时间单位
     */
    public void set(String key, Object value, Long time, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, unit);
    }

    /**
     * 将任意Java对象序列化为JSON并存储到Redis，并设置逻辑过期时间。
     * 用于缓存击穿的解决方案。
     * @param key   键
     * @param value 值
     * @param time  时间
     * @param unit  时间单位
     */
    public void setWithLogicalExpire(String key, Object value, Long time, TimeUnit unit) {
        RedisData redisData = new RedisData(LocalDateTime.now().plusSeconds(unit.toSeconds(time)), value);
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }

    /**
     * 解决【缓存穿透】的查询方法。
     * 逻辑: 查询不到的数据，在Redis中缓存一个空字符串，并设置较短的TTL。
     *
     * @param keyPrefix  缓存key的前缀
     * @param id         业务ID
     * @param type       返回类型
     * @param dbFallback 数据库查询逻辑 (函数式接口)
     * @param time       缓存TTL
     * @param unit       时间单位
     * @param <R>        返回类型
     * @param <ID>       ID类型
     * @return 查询到的数据，或null
     */
    public <R, ID> R queryWithPassThrough(
            String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit) {
        String key = keyPrefix + id;
        // 1. 从Redis查询缓存
        String json = stringRedisTemplate.opsForValue().get(key);
        // 2. 判断是否存在
        if (StrUtil.isNotBlank(json)) {
            // 2.1 存在，直接反序列化返回
            return JSONUtil.toBean(json, type);
        }
        // 2.2 命中的是空值（之前为了防止穿透而缓存的""）
        if (json != null) {
            return null;
        }

        // 3. 不存在，查询数据库
        R r = dbFallback.apply(id);
        // 4. 数据库中也不存在，缓存空值防止穿透
        if (r == null) {
            stringRedisTemplate.opsForValue().set(key, "", RedisConstants.CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }
        // 5. 数据库中存在，写入Redis缓存
        this.set(key, r, time, unit);
        return r;
    }

    /**
     * 解决【缓存击穿】的查询方法 (使用逻辑过期)。
     * 逻辑: 缓存永不过期，但在数据中嵌入一个逻辑过期时间。当发现逻辑过期时，
     * 尝试获取锁并开启独立线程重建缓存，当前请求则直接返回旧数据。
     *
     * @param keyPrefix     缓存key的前缀
     * @param lockKeyPrefix 【已重构】分布式锁的key前缀
     * @param id            业务ID
     * @param type          返回类型
     * @param dbFallback    数据库查询逻辑
     * @param time          逻辑过期TTL
     * @param unit          时间单位
     * @param <R>           返回类型
     * @param <ID>          ID类型
     * @return 查询到的数据，或null
     */
    public <R, ID> R queryWithLogicalExpire(
            String keyPrefix, String lockKeyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit) {
        String key = keyPrefix + id;
        // 1. 从Redis查询缓存
        String json = stringRedisTemplate.opsForValue().get(key);
        // 2. 未命中，直接返回null (这种情况需要提前进行缓存预热)
        if (StrUtil.isBlank(json)) {
            return null;
        }

        // 3. 命中，反序列化为包含逻辑过期时间的对象
        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
        R r = JSONUtil.toBean((JSONObject) redisData.data(), type);
        LocalDateTime expireTime = redisData.expireTime();

        // 4. 判断是否逻辑过期
        if (expireTime.isAfter(LocalDateTime.now())) {
            // 4.1 未过期，直接返回
            return r;
        }

        // 5. 已逻辑过期，需要重建缓存
        // 5.1 尝试获取分布式锁
        String lockKey = lockKeyPrefix + id;
        boolean isLock = tryLock(lockKey);
        if (isLock) {
            // 5.2 获取锁成功，开启独立线程执行缓存重建
            cacheRebuildExecutor.submit(() -> {
                try {
                    // 查询数据库
                    R newR = dbFallback.apply(id);
                    // 重建缓存 (包含新的逻辑过期时间)
                    this.setWithLogicalExpire(key, newR, time, unit);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    // 释放锁
                    unlock(lockKey);
                }
            });
        }
        // 5.3 获取锁失败，或获取成功后，都直接返回旧数据
        return r;
    }

    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", RedisConstants.LOCK_TTL, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }

    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }
}
