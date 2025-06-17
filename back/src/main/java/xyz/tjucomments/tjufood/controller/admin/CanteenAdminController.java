package xyz.tjucomments.tjufood.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import xyz.tjucomments.tjufood.dto.CanteenDTO;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Campus;
import xyz.tjucomments.tjufood.entity.Canteen;
import xyz.tjucomments.tjufood.entity.CanteenType;
import xyz.tjucomments.tjufood.service.ICampusService;
import xyz.tjucomments.tjufood.service.ICanteenService;
import xyz.tjucomments.tjufood.service.ICanteenTypeService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "B02. 内容管理 - 食堂管理", description = "对食堂信息进行增删改查")
@SecurityRequirement(name = "authorization")
@RestController
@RequestMapping("/api/admin/canteens")
public class CanteenAdminController {

    @Resource
    private ICanteenService canteenService;
    @Resource
    private ICampusService campusService;
    @Resource
    private ICanteenTypeService canteenTypeService;

    @Operation(summary = "新增食堂")
    @PostMapping
    public Result addCanteen(@RequestBody Canteen canteen) {
        boolean isSuccess = canteenService.save(canteen);
        return isSuccess ? Result.ok(canteen.getId()) : Result.fail("新增食堂失败！");
    }

    @Operation(summary = "删除食堂")
    @DeleteMapping("/{id}")
    public Result deleteCanteen(@Parameter(description = "食堂ID", required = true) @PathVariable Long id) {
        boolean isSuccess = canteenService.removeById(id);
        return isSuccess ? Result.ok() : Result.fail("删除食堂失败！");
    }

    @Operation(summary = "修改食堂信息")
    @PutMapping
    public Result updateCanteen(@RequestBody Canteen canteen) {
        if (canteen.getId() == null) {
            return Result.fail("更新失败，食堂ID不能为空");
        }
        boolean isSuccess = canteenService.updateById(canteen);
        return isSuccess ? Result.ok() : Result.fail("更新食堂失败！");
    }

    @Operation(summary = "分页查询食堂列表")
    @GetMapping("/list")
    public Result listCanteensForAdmin(
            @Parameter(description = "当前页码") @RequestParam(value = "current", defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        // 1. 创建 QueryWrapper 并添加排序条件，解决SQL语法错误
        QueryWrapper<Canteen> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        // 2. 先进行标准的分页查询
        Page<Canteen> page = canteenService.page(new Page<>(current, size), queryWrapper);
        List<Canteen> records = page.getRecords();
        if (records.isEmpty()) {
            return Result.ok(Collections.emptyList(), 0L);
        }

        // 3. 提取关联ID
        List<Long> campusIds = records.stream().map(Canteen::getCampusId).distinct().collect(Collectors.toList());
        List<Long> typeIds = records.stream().map(Canteen::getTypeId).distinct().collect(Collectors.toList());

        // 4. 一次性查出所有关联的名称
        Map<Long, String> campusMap = campusService.listByIds(campusIds).stream()
                .collect(Collectors.toMap(Campus::getId, Campus::getName));
        Map<Long, String> typeMap = canteenTypeService.listByIds(typeIds).stream()
                .collect(Collectors.toMap(CanteenType::getId, CanteenType::getName));

        // 5. 组装成 DTO 列表
        List<CanteenDTO> dtoList = records.stream().map(canteen -> {
            CanteenDTO dto = CanteenDTO.fromEntity(canteen);
            dto.setCampusName(campusMap.get(canteen.getCampusId()));
            dto.setTypeName(typeMap.get(canteen.getTypeId()));
            return dto;
        }).collect(Collectors.toList());

        // 6. 返回最终结果 - 包装成分页结构
        Map<String, Object> result = new HashMap<>();
        result.put("records", dtoList);
        result.put("total", page.getTotal());
        return Result.ok(result);
    }

    @Operation(summary = "根据ID查询单个食堂")
    @GetMapping("/{id}")
    public Result getCanteenById(@Parameter(description = "食堂ID", required = true) @PathVariable Long id) {
        Canteen canteen = canteenService.getById(id);
        if (canteen == null) {
            return Result.fail("食堂不存在！");
        }
        return Result.ok(canteen);
    }
}