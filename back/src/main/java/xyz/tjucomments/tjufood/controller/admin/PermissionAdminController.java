// 文件路径: src/main/java/xyz/tjucomments/tjufood/controller/admin/PermissionAdminController.java

package xyz.tjucomments.tjufood.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Permission;
import xyz.tjucomments.tjufood.service.IPermissionService; // 你需要创建这个Service接口和实现

import java.util.List;

@Tag(name = "E01. 平台设置 - 权限管理", description = "提供权限查询接口")
@RestController
@RequestMapping("/api/admin/permissions")
public class PermissionAdminController {

    @Resource
    private IPermissionService permissionService;

    @Operation(summary = "查询所有权限列表")
    @GetMapping("/list")
    public Result listAllPermissions() {
        List<Permission> list = permissionService.list();
        return Result.ok(list);
    }
}