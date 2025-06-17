package xyz.tjucomments.tjufood;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class RedisConnectionTest {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    @Test
    void testRedisConnection() {
        try {
            // 1. 测试连接工厂是否正常
            assertNotNull(redisConnectionFactory, "Redis连接工厂不能为空");
            System.out.println("Redis连接工厂创建成功");

            // 2. 测试基本连接
            assertNotNull(stringRedisTemplate, "StringRedisTemplate不能为空");
            System.out.println("StringRedisTemplate注入成功");

            // 3. 定义一个key和value
            String key = "test:connection";
            String value = "success";

            // 4. 尝试向Redis写入数据
            stringRedisTemplate.opsForValue().set(key, value);
            System.out.println("成功向Redis写入数据: " + key + " = " + value);

            // 5. 尝试从Redis读取数据
            String retrievedValue = stringRedisTemplate.opsForValue().get(key);
            System.out.println("从Redis读取到数据: " + key + " = " + retrievedValue);

            // 6. 断言写入和读取的值是否一致
            assertEquals(value, retrievedValue, "写入和读取的值不一致");

            System.out.println("✅ 成功连接到Redis并完成读写测试！");

            // 7. 清理测试数据
            stringRedisTemplate.delete(key);
            System.out.println("测试数据清理完成");

        } catch (Exception e) {
            System.err.println("❌ Redis连接测试失败: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    void testRedisInfo() {
        try {
            // 测试Redis服务器信息
            String info = stringRedisTemplate.getConnectionFactory()
                    .getConnection()
                    .serverCommands()
                    .info()
                    .getProperty("redis_version");
            
            System.out.println("Redis版本信息: " + info);
            assertNotNull(info, "无法获取Redis版本信息");
            
        } catch (Exception e) {
            System.err.println("❌ 获取Redis信息失败: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}