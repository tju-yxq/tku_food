// 文件路径: src/main/java/xyz/tjucomments/tjufood/controller/VoucherController.java
package xyz.tjucomments.tjufood.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import xyz.tjucomments.tjufood.aop.Log;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.service.IVoucherService;

@Tag(name = "08. 优惠券功能", description = "用户查询和兑换优惠券的接口")
@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {

    @Resource
    private IVoucherService voucherService;

    @Operation(summary = "查询指定窗口可用的优惠券列表")
    @GetMapping("/list/{stallId}")
    public Result listVouchersByStall(@Parameter(description = "窗口唯一标识ID") @PathVariable Long stallId) {
        return voucherService.queryVouchersByStallId(stallId);
    }

    @Operation(summary = "兑换（秒杀）一张优惠券")
    @SecurityRequirement(name = "authorization")
    @PostMapping("/redeem/{id}")
    @Log(module = "优惠券模块", operation = "兑换优惠券")
    public Result redeemVoucher(@Parameter(description = "优惠券模板唯一ID") @PathVariable("id") Long voucherId) {
        return voucherService.redeemVoucher(voucherId);
    }

    @Operation(summary = "查询我的优惠券")
    @SecurityRequirement(name = "authorization")
    @GetMapping("/my")
    public Result queryMyVouchers() {
        return voucherService.queryMyVouchers();
    }
}