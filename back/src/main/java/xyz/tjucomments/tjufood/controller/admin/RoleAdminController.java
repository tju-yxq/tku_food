// 文件路径: src/main/java/xyz/tjucomments/tjufood/controller/admin/RoleAdminController.java

package xyz.tjucomments.tjufood.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.dto.RolePermissionDTO;
import xyz.tjucomments.tjufood.entity.Role;
import xyz.tjucomments.tjufood.service.IRoleService;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "E03. 平台设置 - 角色管理", description = "对角色及其权限进行管理")
@SecurityRequirement(name = "authorization")
@RestController
@RequestMapping("/api/admin/roles")
public class RoleAdminController {

    @Resource
    private IRoleService roleService;

    @Operation(summary = "新增角色")
    @PostMapping
    public Result addRole(@RequestBody Role role) {
        boolean isSuccess = roleService.save(role);
        return isSuccess ? Result.ok(role.getId()) : Result.fail("新增角色失败！");
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public Result deleteRole(@Parameter(description = "角色ID") @PathVariable Long id) {
        boolean isSuccess = roleService.removeById(id);
        return isSuccess ? Result.ok() : Result.fail("删除角色失败！");
    }

    @Operation(summary = "修改角色")
    @PutMapping
    public Result updateRole(@RequestBody Role role) {
        if (role.getId() == null) {
            return Result.fail("更新失败，ID不能为空");
        }
        boolean isSuccess = roleService.updateById(role);
        return isSuccess ? Result.ok() : Result.fail("更新角色失败！");
    }

    @Operation(summary = "分页查询角色列表")
    @GetMapping("/list")
    public Result listRoles(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size
    ) {
        // 添加ORDER BY子句以支持SQL Server分页
        Page<Role> page = roleService.page(
            new Page<>(current, size),
            new LambdaQueryWrapper<Role>()
                .orderByAsc(Role::getId) // 改为按ID排序，避免createTime字段问题
        );

        // 返回分页结构
        Map<String, Object> result = new HashMap<>();
        result.put("records", page.getRecords());
        result.put("total", page.getTotal());
        return Result.ok(result);
    }

    @Operation(summary = "查询指定角色的权限ID列表")
    @GetMapping("/{roleId}/permissions")
    public Result getRolePermissions(@Parameter(description = "角色ID") @PathVariable Long roleId) {
        return roleService.getPermissionIdsByRoleId(roleId);
    }

    @Operation(summary = "为角色分配权限")
    @PostMapping("/permissions")
    public Result assignPermissions(@RequestBody RolePermissionDTO rolePermissionDTO) {
        return roleService.assignPermissionsToRole(rolePermissionDTO.getRoleId(), rolePermissionDTO.getPermissionIds());
    }
}