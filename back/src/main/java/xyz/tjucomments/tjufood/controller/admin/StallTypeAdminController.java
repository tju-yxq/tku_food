package xyz.tjucomments.tjufood.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Stall;
import xyz.tjucomments.tjufood.entity.StallType;
import xyz.tjucomments.tjufood.service.IStallService;
import xyz.tjucomments.tjufood.service.IStallTypeService;

import java.util.List;

@Tag(name = "B05. 内容管理 - 窗口类型管理", description = "对窗口类型信息进行增删改查")
@SecurityRequirement(name = "authorization")
@RestController
@RequestMapping("/api/admin/stall-types")
public class StallTypeAdminController {

    @Resource
    private IStallTypeService stallTypeService;

    @Resource
    private IStallService stallService;

    @Operation(summary = "新增窗口类型")
    @PostMapping
    public Result addStallType(@RequestBody StallType stallType) {
        // ID由数据库自增，无需程序生成
        boolean isSuccess = stallTypeService.save(stallType);
        return isSuccess ? Result.ok(stallType.getId()) : Result.fail("新增窗口类型失败！");
    }

    @Operation(summary = "删除窗口类型")
    @DeleteMapping("/{id}")
    public Result deleteStallType(@Parameter(description = "窗口类型ID", required = true) @PathVariable Long id) {
        // 检查是否有窗口使用此类型
        long count = stallService.count(new QueryWrapper<Stall>().eq("type_id", id));
        if (count > 0) {
            return Result.fail("该窗口类型正在被 " + count + " 个窗口使用，无法删除！请先删除相关窗口。");
        }

        boolean isSuccess = stallTypeService.removeById(id);
        return isSuccess ? Result.ok() : Result.fail("删除窗口类型失败！");
    }

    @Operation(summary = "修改窗口类型信息")
    @PutMapping
    public Result updateStallType(@RequestBody StallType stallType) {
        if (stallType.getId() == null) {
            return Result.fail("更新失败，ID不能为空");
        }
        boolean isSuccess = stallTypeService.updateById(stallType);
        return isSuccess ? Result.ok() : Result.fail("更新窗口类型失败！");
    }

    @Operation(summary = "查询所有窗口类型列表")
    @GetMapping("/list")
    public Result listAllStallTypes() {
        // 添加排序，避免SQL Server分页问题
        List<StallType> list = stallTypeService.list(
            new QueryWrapper<StallType>().orderByAsc("id")
        );
        return Result.ok(list);
    }
}