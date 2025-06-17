package xyz.tjucomments.tjufood.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Dish;
import xyz.tjucomments.tjufood.service.IDishService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Tag(name = "04. 菜品管理", description = "菜品信息的增删改查及搜索接口")
@RestController
@RequestMapping("/api/dishes")
public class DishController {
    @Resource
    private IDishService dishService;

    @Operation(summary = "分页搜索菜品")
    @GetMapping("/search")
    public Result searchDishes(
            @Parameter(description = "菜品名称关键词") @RequestParam(value = "name", required = false) String name,
            @Parameter(description = "当前页码") @RequestParam(value = "current", defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(value = "size", defaultValue = "10") Integer size,
            @Parameter(description = "排序字段 (name=按名字, score=按评分, price=按价格)", example = "score")
            @RequestParam(value = "sortBy", defaultValue = "score") String sortBy,
            @Parameter(description = "排序方向 (asc=升序, desc=降序)", example = "desc")
            @RequestParam(value = "sortOrder", defaultValue = "desc") String sortOrder) {
        return dishService.searchDishesByName(name, current, size, sortBy, sortOrder);
    }

    @Operation(summary = "查询窗口下的所有菜品")
    @GetMapping("/stall/{stallId}")
    public Result listDishesByStallId(@Parameter(description = "所属窗口的ID", required = true) @PathVariable Long stallId) {
        return dishService.listDishesByStallId(stallId);
    }

    @Operation(summary = "查询单个菜品详情")
    @GetMapping("/{id}")
    public Result getDishById(@Parameter(description = "菜品的唯一ID", required = true) @PathVariable Long id) {
        return dishService.queryDishById(id);
    }



}
