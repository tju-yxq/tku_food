// src/main/java/xyz/tjucomments/tjufood/service/IVoucherService.java
package xyz.tjucomments.tjufood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Voucher;

/**
 * 优惠券服务接口
 */
public interface IVoucherService extends IService<Voucher> {

    /**
     * 查询指定窗口可用的优惠券
     * @param stallId 窗口ID
     * @return 优惠券列表
     */
    Result queryVouchersByStallId(Long stallId);

    /**
     * 兑换优惠券（秒杀场景）
     * @param voucherId 优惠券模板ID
     * @return 操作结果
     */
    Result redeemVoucher(Long voucherId);

    /**
     * 查询我的优惠券
     * @return 我的优惠券列表
     */
    Result queryMyVouchers();

    /**
     * 【修正】创建优惠券订单的事务性方法 (移除了错误的@Override注解)
     * @param voucherId 优惠券ID
     * @return 操作结果
     */
    Result createVoucherOrder(Long voucherId);
}