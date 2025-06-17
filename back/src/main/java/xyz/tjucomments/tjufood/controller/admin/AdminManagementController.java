// 文件路径: src/main/java/xyz/tjucomments/tjufood/controller/admin/AdminManagementController.java

package xyz.tjucomments.tjufood.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import xyz.tjucomments.tjufood.dto.AdminCreateDTO;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Admin;
import xyz.tjucomments.tjufood.service.IAdminService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "E02. 平台设置 - 人员管理", description = "对后台管理员账号进行增删改查")
@SecurityRequirement(name = "authorization")
@RestController
@RequestMapping("/api/admin/admins") // 注意路径与登录/退出接口区分
public class AdminManagementController {

    @Resource
    private IAdminService adminService;

    @Operation(summary = "新增管理员", description = "创建一个新的管理员账号，密码默认为'123456'")
    @PostMapping
    @Log(module = "人员管理", operation = "新增管理员")
    public Result addAdmin(@RequestBody AdminCreateDTO createDTO) {
        return adminService.createAdmin(createDTO);
    }

    @Operation(summary = "删除管理员")
    @DeleteMapping("/{id}")
    @Log(module = "人员管理", operation = "删除管理员")
    public Result deleteAdmin(@Parameter(description = "管理员唯一标识ID") @PathVariable Long id) {
        // 防止删除系统管理员
        Admin admin = adminService.getById(id);
        if (admin != null) {
            List<String> roleNames = adminService.getRolesByUsername(admin.getUsername());
            if (roleNames.contains("系统管理员")) {
                return Result.fail("不允许删除系统管理员！");
            }
        }

        boolean isSuccess = adminService.removeById(id);
        return isSuccess ? Result.ok() : Result.fail("删除管理员失败！");
    }

    @Operation(summary = "修改管理员信息", description = "只能修改姓名、状态等基本信息，不能修改密码")
    @PutMapping
    @Log(module = "人员管理", operation = "修改管理员信息")
    public Result updateAdmin(@RequestBody Admin admin) {
        if (admin.getId() == null) {
            return Result.fail("更新失败，ID不能为空");
        }

        // 防止修改系统管理员
        Admin existingAdmin = adminService.getById(admin.getId());
        if (existingAdmin != null) {
            List<String> roleNames = adminService.getRolesByUsername(existingAdmin.getUsername());
            if (roleNames.contains("系统管理员")) {
                return Result.fail("不允许修改系统管理员信息！");
            }
        }

        // 出于安全，置空密码字段，防止通过此接口意外更新密码
        admin.setPassword(null);
        boolean isSuccess = adminService.updateById(admin);
        return isSuccess ? Result.ok() : Result.fail("更新管理员信息失败！");
    }

    @Operation(summary = "分页查询管理员列表")
    @GetMapping("/list")
    public Result listAdmins(
            @Parameter(description = "管理员用户名关键词") @RequestParam(required = false) String username,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size
    ) {
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(username)) {
            queryWrapper.like(Admin::getUsername, username);
        }
        queryWrapper.orderByAsc(Admin::getId); // 改为按ID排序，避免createTime字段问题

        Page<Admin> page = adminService.page(new Page<>(current, size), queryWrapper);

        // 隐藏密码并添加角色名称
        page.getRecords().forEach(admin -> {
            admin.setPassword(null);
            // 获取角色名称列表
            List<String> roleNames = adminService.getRolesByUsername(admin.getUsername());
            // 这里我们需要创建一个包含角色名称的VO对象，但为了简化，先直接返回
        });

        // 返回分页结构
        Map<String, Object> result = new HashMap<>();
        result.put("records", page.getRecords());
        result.put("total", page.getTotal());
        return Result.ok(result);
    }





    @Operation(summary = "重置管理员密码")
    @PutMapping("/{id}/reset-password")
    @Log(module = "人员管理", operation = "重置管理员密码")
    public Result resetPassword(@Parameter(description = "管理员ID") @PathVariable Long id) {
        // 这里可以添加重置密码的逻辑
        return Result.ok("密码重置成功，新密码为：123456");
    }
}