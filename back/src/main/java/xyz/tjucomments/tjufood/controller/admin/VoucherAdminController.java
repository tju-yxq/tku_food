// 文件路径: src/main/java/xyz/tjucomments/tjufood/controller/admin/VoucherAdminController.java

package xyz.tjucomments.tjufood.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Voucher;
import xyz.tjucomments.tjufood.service.IVoucherService;

@Tag(name = "D02. 运营管理 - 优惠券管理", description = "对优惠券模板进行增删改查")
@SecurityRequirement(name = "authorization")
@RestController
@RequestMapping("/api/admin/vouchers")
public class VoucherAdminController {

    @Resource
    private IVoucherService voucherService;

    @Operation(summary = "新增优惠券模板")
    @PostMapping
    public Result addVoucher(@RequestBody Voucher voucher) {
        // ID由数据库自增
        boolean isSuccess = voucherService.save(voucher);
        return isSuccess ? Result.ok(voucher.getId()) : Result.fail("新增优惠券失败！");
    }

    @Operation(summary = "删除优惠券模板")
    @DeleteMapping("/{id}")
    public Result deleteVoucher(@Parameter(description = "优惠券模板唯一ID") @PathVariable Long id) {
        boolean isSuccess = voucherService.removeById(id);
        return isSuccess ? Result.ok() : Result.fail("删除优惠券失败！");
    }

    @Operation(summary = "修改优惠券模板")
    @PutMapping
    public Result updateVoucher(@RequestBody Voucher voucher) {
        if (voucher.getId() == null) {
            return Result.fail("更新失败，ID不能为空");
        }
        boolean isSuccess = voucherService.updateById(voucher);
        return isSuccess ? Result.ok() : Result.fail("更新优惠券失败！");
    }

    @Operation(summary = "分页查询优惠券模板列表")
    @GetMapping("/list")
    public Result listVouchers(
            @Parameter(description = "优惠券标题关键词") @RequestParam(required = false) String title,
            @Parameter(description = "优惠券状态 (1=正常, 2=暂停)") @RequestParam(required = false) Integer status,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size
    ) {
        QueryWrapper<Voucher> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(title)) {
            queryWrapper.like("title", title);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("create_time");

        Page<Voucher> page = voucherService.page(new Page<>(current, size), queryWrapper);
        return Result.ok(page.getRecords(), page.getTotal());
    }
}