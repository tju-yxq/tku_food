// src/main/java/xyz/tjucomments/tjufood/service/impl/VoucherServiceImpl.java
package xyz.tjucomments.tjufood.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.aop.framework.AopContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.UserVoucher;
import xyz.tjucomments.tjufood.entity.Voucher;
import xyz.tjucomments.tjufood.interceptor.UserHolder;
import xyz.tjucomments.tjufood.mapper.VoucherMapper;
import xyz.tjucomments.tjufood.service.IUserVoucherService;
import xyz.tjucomments.tjufood.service.IVoucherService;
import xyz.tjucomments.tjufood.utils.constants.IdPrefixConstants;
import xyz.tjucomments.tjufood.utils.id.RedisIdWorker;
import xyz.tjucomments.tjufood.utils.lock.ILock;
import xyz.tjucomments.tjufood.utils.lock.SimpleRedisLock;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements IVoucherService {

    @Resource
    private IUserVoucherService userVoucherService;
    @Resource
    private RedisIdWorker redisIdWorker;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ExecutorService cacheRebuildExecutor; // 使用已有的线程池

    // Redis Lua脚本，用于原子性地扣减库存和判断购买资格
    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("lua/seckill.lua")); // 假设您有这个lua脚本
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    @Override
    public Result queryVouchersByStallId(Long stallId) {
        List<Voucher> vouchers = query().eq("status", 1).list();
        return Result.ok(vouchers);
    }

    @Override
    public Result redeemVoucher(Long voucherId) {
        Long userId = UserHolder.getUser().getId();
        long orderId = redisIdWorker.nextId(IdPrefixConstants.ORDER_ID_PREFIX);

        // 1. 执行Lua脚本
        Long result = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                voucherId.toString(), userId.toString(), String.valueOf(orderId)
        );
        int r = result.intValue();
        // 2. 判断结果
        if (r != 0) {
            return Result.fail(r == 1 ? "库存不足" : "不能重复下单");
        }

        // 3. 获取代理对象，用于后续的异步数据库操作
        IVoucherService proxy = (IVoucherService) AopContext.currentProxy();
        // 4. 返回订单ID
        return Result.ok(orderId);
    }

    @Transactional
    @Override // 新增接口的实现
    public Result createVoucherOrder(Long voucherId) {
        Long userId = UserHolder.getUser().getId();

        // 1. 一人一单校验
        long count = userVoucherService.query().eq("user_id", userId).eq("voucher_id", voucherId).count();
        if (count > 0) {
            return Result.fail("您已经抢过这张优惠券了！");
        }

        // 2. 扣减库存 (乐观锁)
        boolean success = update()
                .setSql("stock = stock - 1")
                .eq("id", voucherId)
                .gt("stock", 0)
                .update();
        if (!success) {
            return Result.fail("手慢了，优惠券已被抢光！");
        }

        // 3. 创建用户优惠券记录
        UserVoucher userVoucher = new UserVoucher();
        long orderId = redisIdWorker.nextId(IdPrefixConstants.ORDER_ID_PREFIX);
        userVoucher.setId(orderId);
        userVoucher.setUserId(userId);
        userVoucher.setVoucherId(voucherId);
        userVoucherService.save(userVoucher);

        return Result.ok(orderId);
    }

    @Override
    public Result queryMyVouchers() {
        Long userId = UserHolder.getUser().getId();
        List<UserVoucher> vouchers = userVoucherService.query().eq("user_id", userId).list();
        return Result.ok(vouchers);
    }
}