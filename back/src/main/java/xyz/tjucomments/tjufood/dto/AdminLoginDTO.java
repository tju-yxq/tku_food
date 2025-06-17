package xyz.tjucomments.tjufood.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminLoginDTO {
    @NotBlank(message = "管理员账号不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}