// src/main/java/xyz/tjucomments/tjufood/service/IUserService.java

package xyz.tjucomments.tjufood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.tjucomments.tjufood.dto.*;
import xyz.tjucomments.tjufood.entity.User;

public interface IUserService extends IService<User> {
    // --- 已有方法 (保持不变) ---
    Result sendCode(SendCodeDTO sendCodeDTO);
    Result register(RegisterFormDTO registerForm);
    Result login(LoginFormDTO loginForm);
    Result resetPassword(PasswordResetDTO resetForm);
    Result logout(String token);

    // --- 新增方法 ---

    /**
     * 获取当前登录用户的详细信息
     * @return 包含用户详细信息的Result对象
     */
    Result queryMe();

    /**
     * 根据ID查询指定用户的公开信息
     * @param userId 用户ID
     * @return 包含用户公开信息的Result对象
     */
    Result queryUserById(Long userId);

    /**
     * 更新当前登录用户的个人资料
     * @param userDTO 包含待更新信息的用户DTO
     * @return 操作结果
     */
    Result updateUserProfile(UserDTO userDTO);

    /**
     * 用户签到
     * @return 签到结果，成功则返回连续签到天数
     */
    Result sign();
}