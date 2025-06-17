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
import xyz.tjucomments.tjufood.dto.DishDTO;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Canteen;
import xyz.tjucomments.tjufood.entity.Dish;
import xyz.tjucomments.tjufood.entity.Stall;
import xyz.tjucomments.tjufood.service.ICanteenService;
import xyz.tjucomments.tjufood.service.IDishService;
import xyz.tjucomments.tjufood.service.IStallService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "B04. 内容管理 - 菜品管理", description = "对菜品信息进行增删改查")
@SecurityRequirement(name = "authorization")
@RestController
@RequestMapping("/api/admin/dishes")
public class DishAdminController {

    @Resource
    private IDishService dishService;
    @Resource
    private IStallService stallService;
    @Resource
    private ICanteenService canteenService;

    @Log(module = "菜品管理", operation = "新增菜品")
    @Operation(summary = "新增菜品")
    @PostMapping
    public Result addDish(@RequestBody Dish dish) {
        // 验证必需字段
        if (dish.getStallId() == null) {
            return Result.fail("所属窗口ID不能为空！");
        }
        if (!StringUtils.hasText(dish.getName())) {
            return Result.fail("菜品名称不能为空！");
        }
        if (dish.getPrice() == null) {
            return Result.fail("菜品价格不能为空！");
        }

        // 验证窗口是否存在
        Stall stall = stallService.getById(dish.getStallId());
        if (stall == null) {
            return Result.fail("指定的窗口不存在！");
        }

        boolean isSuccess = dishService.save(dish);
        return isSuccess ? Result.ok(dish.getId()) : Result.fail("新增菜品失败！");
    }

    @Log(module = "菜品管理", operation = "删除菜品")
    @Operation(summary = "删除菜品")
    @DeleteMapping("/{id}")
    public Result deleteDish(@Parameter(description = "菜品ID", required = true) @PathVariable Long id) {
        boolean isSuccess = dishService.removeById(id);
        return isSuccess ? Result.ok() : Result.fail("删除菜品失败！");
    }

    @Log(module = "菜品管理", operation = "修改菜品")
    @Operation(summary = "修改菜品信息")
    @PutMapping
    public Result updateDish(@RequestBody Dish dish) {
        if (dish.getId() == null) {
            return Result.fail("更新失败，菜品ID不能为空");
        }

        // 验证菜品是否存在
        Dish existingDish = dishService.getById(dish.getId());
        if (existingDish == null) {
            return Result.fail("菜品不存在！");
        }

        // 如果更新了窗口ID，验证新窗口是否存在
        if (dish.getStallId() != null && !dish.getStallId().equals(existingDish.getStallId())) {
            Stall stall = stallService.getById(dish.getStallId());
            if (stall == null) {
                return Result.fail("指定的窗口不存在！");
            }
        }

        boolean isSuccess = dishService.updateById(dish);
        return isSuccess ? Result.ok() : Result.fail("更新菜品失败！");
    }

    @Operation(summary = "分页查询菜品列表（后台管理）")
    @GetMapping("/list")
    public Result listDishesForAdmin(
            @Parameter(description = "菜品名称关键字") @RequestParam(value = "name", required = false) String name,
            @Parameter(description = "当前页码") @RequestParam(value = "current", defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        QueryWrapper<Dish> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(name)) {
            queryWrapper.like("name", name);
        }
        queryWrapper.orderByDesc("id");

        Page<Dish> page = dishService.page(new Page<>(current, size), queryWrapper);
        List<Dish> records = page.getRecords();
        if (records.isEmpty()) {
            return Result.ok(Collections.emptyList(), 0L);
        }

        // 提取所有窗口ID
        List<Long> stallIds = records.stream().map(Dish::getStallId).distinct().collect(Collectors.toList());
        Map<Long, Stall> stallMap = stallService.listByIds(stallIds).stream()
                .collect(Collectors.toMap(Stall::getId, stall -> stall));

        // 提取所有食堂ID
        List<Long> canteenIds = stallMap.values().stream().map(Stall::getCanteenId).distinct().collect(Collectors.toList());
        Map<Long, String> canteenMap = canteenService.listByIds(canteenIds).stream()
                .collect(Collectors.toMap(Canteen::getId, Canteen::getName));

        // 组装DTO
        List<DishDTO> dtoList = records.stream().map(dish -> {
            DishDTO dto = DishDTO.fromEntity(dish);
            Stall stall = stallMap.get(dish.getStallId());
            if (stall != null) {
                dto.setStallName(stall.getName());
                dto.setCanteenName(canteenMap.getOrDefault(stall.getCanteenId(), "未知食堂"));
            } else {
                dto.setStallName("未知窗口");
                dto.setCanteenName("未知食堂");
            }
            return dto;
        }).collect(Collectors.toList());

        // 返回分页结构
        Map<String, Object> result = new HashMap<>();
        result.put("records", dtoList);
        result.put("total", page.getTotal());
        return Result.ok(result);
    }

    @Operation(summary = "根据ID查询单个菜品详情")
    @GetMapping("/{id}")
    public Result getDishById(@Parameter(description = "菜品ID", required = true) @PathVariable Long id) {
        Dish dish = dishService.getById(id);
        if (dish == null) {
            return Result.fail("菜品不存在！");
        }
        return Result.ok(dish);
    }
}