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
import xyz.tjucomments.tjufood.aop.Log;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.dto.StallDTO;
import xyz.tjucomments.tjufood.entity.Canteen;
import xyz.tjucomments.tjufood.entity.Stall;
import xyz.tjucomments.tjufood.entity.StallType;
import xyz.tjucomments.tjufood.service.ICanteenService;
import xyz.tjucomments.tjufood.service.IStallService;
import xyz.tjucomments.tjufood.service.IStallTypeService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "B03. 内容管理 - 窗口管理", description = "对窗口信息进行增删改查")
@SecurityRequirement(name = "authorization")
@RestController
@RequestMapping("/api/admin/stalls")
public class StallAdminController {

    @Resource
    private IStallService stallService;
    @Resource
    private ICanteenService canteenService;
    @Resource
    private IStallTypeService stallTypeService;

    @Log(module = "窗口管理", operation = "新增窗口")
    @Operation(summary = "新增窗口")
    @PostMapping
    public Result addStall(@RequestBody Stall stall) {
        // 验证必需字段
        if (!StringUtils.hasText(stall.getName())) {
            return Result.fail("窗口名称不能为空！");
        }
        if (stall.getCanteenId() == null) {
            return Result.fail("所属食堂ID不能为空！");
        }
        if (stall.getTypeId() == null) {
            return Result.fail("窗口类型ID不能为空！");
        }

        // 验证食堂是否存在
        Canteen canteen = canteenService.getById(stall.getCanteenId());
        if (canteen == null) {
            return Result.fail("指定的食堂不存在！");
        }

        // 验证窗口类型是否存在
        StallType stallType = stallTypeService.getById(stall.getTypeId());
        if (stallType == null) {
            return Result.fail("指定的窗口类型不存在！");
        }

        boolean isSuccess = stallService.save(stall);
        return isSuccess ? Result.ok(stall.getId()) : Result.fail("新增窗口失败！");
    }

    @Log(module = "窗口管理", operation = "删除窗口")
    @Operation(summary = "删除窗口")
    @DeleteMapping("/{id}")
    public Result deleteStall(@Parameter(description = "窗口ID", required = true) @PathVariable Long id) {
        boolean isSuccess = stallService.removeById(id);
        return isSuccess ? Result.ok() : Result.fail("删除窗口失败！");
    }

    @Log(module = "窗口管理", operation = "修改窗口")
    @Operation(summary = "修改窗口信息")
    @PutMapping
    public Result updateStall(@RequestBody Stall stall) {
        if (stall.getId() == null) {
            return Result.fail("更新失败，窗口ID不能为空");
        }

        // 验证窗口是否存在
        Stall existingStall = stallService.getById(stall.getId());
        if (existingStall == null) {
            return Result.fail("窗口不存在！");
        }

        // 如果更新了食堂ID，验证新食堂是否存在
        if (stall.getCanteenId() != null && !stall.getCanteenId().equals(existingStall.getCanteenId())) {
            Canteen canteen = canteenService.getById(stall.getCanteenId());
            if (canteen == null) {
                return Result.fail("指定的食堂不存在！");
            }
        }

        // 如果更新了窗口类型ID，验证新类型是否存在
        if (stall.getTypeId() != null && !stall.getTypeId().equals(existingStall.getTypeId())) {
            StallType stallType = stallTypeService.getById(stall.getTypeId());
            if (stallType == null) {
                return Result.fail("指定的窗口类型不存在！");
            }
        }

        boolean isSuccess = stallService.updateById(stall);
        return isSuccess ? Result.ok() : Result.fail("更新窗口失败！");
    }

    @Operation(summary = "分页查询窗口列表")
    @GetMapping("/list")
    public Result listStallsForAdmin(
            @Parameter(description = "当前页码") @RequestParam(value = "current", defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        QueryWrapper<Stall> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id"); // 添加排序

        Page<Stall> page = stallService.page(new Page<>(current, size), queryWrapper);
        List<Stall> records = page.getRecords();
        if (records.isEmpty()) {
            return Result.ok(Collections.emptyList(), 0L);
        }

        // 提取关联ID
        List<Long> canteenIds = records.stream().map(Stall::getCanteenId).distinct().collect(Collectors.toList());
        List<Long> typeIds = records.stream().map(Stall::getTypeId).distinct().collect(Collectors.toList());

        // 一次性查出所有关联的名称
        Map<Long, String> canteenMap = canteenService.listByIds(canteenIds).stream()
                .collect(Collectors.toMap(Canteen::getId, Canteen::getName));
        Map<Long, String> stallTypeMap = stallTypeService.listByIds(typeIds).stream()
                .collect(Collectors.toMap(StallType::getId, StallType::getName));

        // 组装成 DTO 列表
        List<StallDTO> dtoList = records.stream().map(stall -> {
            StallDTO dto = StallDTO.fromEntity(stall);
            // 安全地设置关联名称，避免空指针异常
            dto.setCanteenName(canteenMap.getOrDefault(stall.getCanteenId(), "未知食堂"));
            dto.setTypeName(stallTypeMap.getOrDefault(stall.getTypeId(), "未知类型"));
            return dto;
        }).collect(Collectors.toList());

        // 返回分页结构
        Map<String, Object> result = new HashMap<>();
        result.put("records", dtoList);
        result.put("total", page.getTotal());
        return Result.ok(result);
    }

    @Operation(summary = "根据ID查询单个窗口")
    @GetMapping("/{id}")
    public Result getStallById(@Parameter(description = "窗口唯一标识ID", required = true) @PathVariable Long id) {
        Stall stall = stallService.getById(id);
        if (stall == null) {
            return Result.fail("窗口不存在！");
        }
        return Result.ok(stall);
    }
}