// 文件路径: src/main/java/xyz/tjucomments/tjufood/controller/admin/BannerAdminController.java

package xyz.tjucomments.tjufood.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Banner;
import xyz.tjucomments.tjufood.service.IBannerService;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "D01. 运营管理 - 轮播图管理", description = "对首页等位置的轮播图进行管理")
@SecurityRequirement(name = "authorization")
@RestController
@RequestMapping("/api/admin/banners")
public class BannerAdminController {

    @Resource
    private IBannerService bannerService;

    @Operation(summary = "新增轮播图")
    @PostMapping
    public Result addBanner(@RequestBody Banner banner) {
        // ID由数据库自增
        boolean isSuccess = bannerService.save(banner);
        return isSuccess ? Result.ok(banner.getId()) : Result.fail("新增轮播图失败！");
    }

    @Operation(summary = "删除轮播图")
    @DeleteMapping("/{id}")
    public Result deleteBanner(@Parameter(description = "轮播图ID") @PathVariable Long id) {
        boolean isSuccess = bannerService.removeById(id);
        return isSuccess ? Result.ok() : Result.fail("删除轮播图失败！");
    }

    @Operation(summary = "修改轮播图")
    @PutMapping
    public Result updateBanner(@RequestBody Banner banner) {
        if (banner.getId() == null) {
            return Result.fail("更新失败，ID不能为空");
        }
        boolean isSuccess = bannerService.updateById(banner);
        return isSuccess ? Result.ok() : Result.fail("更新轮播图失败！");
    }

    @Operation(summary = "分页查询轮播图列表")
    @GetMapping("/list")
    public Result listBanners(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {

        // 按排序值升序、创建时间降序进行排序
        QueryWrapper<Banner> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort").orderByDesc("create_time");

        Page<Banner> page = bannerService.page(new Page<>(current, size), queryWrapper);

        // 返回分页结构
        Map<String, Object> result = new HashMap<>();
        result.put("records", page.getRecords());
        result.put("total", page.getTotal());
        return Result.ok(result);
    }
}