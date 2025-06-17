package xyz.tjucomments.tjufood.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.CanteenType;
import xyz.tjucomments.tjufood.service.ICanteenTypeService;

import java.util.List;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Tag(name = "B06. 内容管理 - 食堂类型管理", description = "对食堂类型信息进行增删改查")
@SecurityRequirement(name = "authorization")
@RestController
@RequestMapping("/api/admin/canteen-types")
public class CanteenTypeAdminController {

    @Resource
    private ICanteenTypeService canteenTypeService;

    @Operation(summary = "新增食堂类型")
    @PostMapping
    public Result addCanteenType(@RequestBody CanteenType canteenType) {
        boolean isSuccess = canteenTypeService.save(canteenType);
        return isSuccess ? Result.ok(canteenType.getId()) : Result.fail("新增食堂类型失败！");
    }

    @Operation(summary = "删除食堂类型")
    @DeleteMapping("/{id}")
    public Result deleteCanteenType(@Parameter(description = "食堂类型ID", required = true) @PathVariable Long id) {
        boolean isSuccess = canteenTypeService.removeById(id);
        return isSuccess ? Result.ok() : Result.fail("删除食堂类型失败！");
    }

    @Operation(summary = "修改食堂类型信息")
    @PutMapping
    public Result updateCanteenType(@RequestBody CanteenType canteenType) {
        if (canteenType.getId() == null) {
            return Result.fail("更新失败，ID不能为空");
        }
        boolean isSuccess = canteenTypeService.updateById(canteenType);
        return isSuccess ? Result.ok() : Result.fail("更新食堂类型失败！");
    }

    @Operation(summary = "查询所有食堂类型列表")
    @GetMapping("/list")
    public Result listAllCanteenTypes() {
        List<CanteenType> list = canteenTypeService.list();
        return Result.ok(list);
    }
}