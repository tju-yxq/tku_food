// src/main/java/xyz/tjucomments/tjufood/controller/UserController.java

package xyz.tjucomments.tjufood.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import xyz.tjucomments.tjufood.aop.Log;
import xyz.tjucomments.tjufood.dto.LoginFormDTO;
import xyz.tjucomments.tjufood.dto.PasswordResetDTO;
import xyz.tjucomments.tjufood.dto.RegisterFormDTO;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.dto.UserDTO;
import xyz.tjucomments.tjufood.service.IUserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Tag(name = "01. 用户功能", description = "用户登录、注册、个人信息、签到等接口")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Resource
    private IUserService userService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result login(@RequestBody LoginFormDTO loginForm) {
        return userService.login(loginForm);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result register(@RequestBody RegisterFormDTO registerForm) {
        return userService.register(registerForm);
    }

    @Operation(summary = "重置密码")
    @PostMapping("/password")
    public Result resetPassword(@RequestBody PasswordResetDTO resetForm) {
        return userService.resetPassword(resetForm);
    }

    @Operation(summary = "用户退出登录")
    @SecurityRequirement(name = "authorization")
    @PostMapping("/logout")
    public Result logout(@Parameter(description = "用户登录令牌") @RequestHeader("authorization") String token) {
        return userService.logout(token);
    }

    @Operation(summary = "获取当前登录用户的详细信息")
    @SecurityRequirement(name = "authorization")
    @GetMapping("/me")
    public Result me() {
        return userService.queryMe();
    }

    @Operation(summary = "根据ID查询指定用户的公开信息")
    @GetMapping("/{id}")
    public Result queryUserById(@Parameter(description = "用户唯一标识ID") @PathVariable("id") Long userId) {
        return userService.queryUserById(userId);
    }

    @Operation(summary = "编辑个人资料")
    @SecurityRequirement(name = "authorization")
    @PutMapping("/me")
    @Log(module = "用户模块", operation = "修改个人资料")
    public Result updateUserProfile(@RequestBody UserDTO userDTO) {
        return userService.updateUserProfile(userDTO);
    }

    @Operation(summary = "用户签到")
    @SecurityRequirement(name = "authorization")
    @PostMapping("/sign")
    @Log(module = "用户模块", operation = "用户签到")
    public Result sign() {
        return userService.sign();
    }
}