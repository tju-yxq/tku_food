package xyz.tjucomments.tjufood.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import xyz.tjucomments.tjufood.aop.Log;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Campus;
import xyz.tjucomments.tjufood.service.ICampusService;

import java.util.List;

/**
 * @author Gemini
 * @description 校区管理后台接口 (已重构)
 * @create 2025-06-16 14:15
 */
@Tag(name = "B01. 内容管理 - 校区管理", description = "对校区信息进行增删改查")
@SecurityRequirement(name = "authorization")
@RestController
@RequestMapping("/api/admin/campuses")
public class CampusAdminController {

    @Resource
    private ICampusService campusService;

    // 【已移除】不再需要RedisIdWorker，因为ID由数据库自增

    @Log(module = "校区管理", operation = "新增校区")
    @Operation(summary = "新增校区")
    @PostMapping
    public Result addCampus(@RequestBody Campus campus) {
        // 【高优先级修复】移除手动生成ID的逻辑，因为数据库是自增的
        // campus.setId(id) 等代码已删除
        boolean isSuccess = campusService.save(campus);
        return isSuccess ? Result.ok(campus.getId()) : Result.fail("新增校区失败！");
    }

    @Log(module = "校区管理", operation = "删除校区")
    @Operation(summary = "删除校区")
    @DeleteMapping("/{id}")
    public Result deleteCampus(@Parameter(description = "校区ID", required = true) @PathVariable Long id) {
        boolean isSuccess = campusService.removeById(id);
        return isSuccess ? Result.ok() : Result.fail("删除校区失败！");
    }

    @Log(module = "校区管理", operation = "修改校区")
    @Operation(summary = "修改校区信息")
    @PutMapping
    public Result updateCampus(@RequestBody Campus campus) {
        if (campus.getId() == null) {
            return Result.fail("更新失败，校区ID不能为空");
        }
        boolean isSuccess = campusService.updateById(campus);
        return isSuccess ? Result.ok() : Result.fail("更新校区失败！");
    }

    @Operation(summary = "查询所有校区列表")
    @GetMapping("/list")
    public Result listAllCampuses() {
        List<Campus> list = campusService.list();
        return Result.ok(list);
    }

    @Operation(summary = "根据ID查询单个校区")
    @GetMapping("/{id}")
    public Result getCampusById(@Parameter(description = "校区唯一标识ID", required = true) @PathVariable Long id) {
        Campus campus = campusService.getById(id);
        if (campus == null) {
            return Result.fail("校区不存在！");
        }
        return Result.ok(campus);
    }
}
