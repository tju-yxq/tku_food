package xyz.tjucomments.tjufood.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import xyz.tjucomments.tjufood.dto.AdminDTO;
import xyz.tjucomments.tjufood.dto.AdminLoginDTO;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.service.IAdminService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import xyz.tjucomments.tjufood.dto.AdminInfoVO;
import xyz.tjucomments.tjufood.interceptor.AdminHolder;

@Tag(name = "A01. 后台管理", description = "管理员登录、权限等相关接口")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Resource
    private IAdminService adminService;

    @Operation(summary = "管理员登录", description = "使用管理员账号和密码登录，成功后返回Token")
    @PostMapping("/login")
    public Result login(@RequestBody AdminLoginDTO loginForm) {
        // 调用新的login方法并用Result.ok()包装
        AdminInfoVO adminInfoVO = adminService.login(loginForm);
        return Result.ok(adminInfoVO);
    }
    @Operation(summary = "获取当前登录的管理员信息")
    @SecurityRequirement(name = "authorization")
    @GetMapping("/me")
    public Result getAdminInfo() {
        AdminDTO admin = AdminHolder.getAdmin();
        if (admin == null) {
            return Result.fail("用户未登录");
        }
        // 同样封装成AdminInfoVO返回，保持数据结构一致
        AdminInfoVO vo = new AdminInfoVO();
        vo.setId(admin.getId());
        vo.setUsername(admin.getUsername());
        vo.setName(admin.getName());
        vo.setRoles(adminService.getRolesByUsername(admin.getUsername()));
        // ... 其他信息
        return Result.ok(vo);
    }
    @Operation(summary = "管理员退出登录")
    @SecurityRequirement(name = "authorization")
    @PostMapping("/logout")
    public Result logout(@RequestHeader("authorization") String token) {
        return adminService.logout(token);
    }
}