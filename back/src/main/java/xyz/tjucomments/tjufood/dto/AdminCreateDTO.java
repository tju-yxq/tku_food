package xyz.tjucomments.tjufood.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull; // 新增导入
import lombok.Data;

@Data
@Schema(description = "创建管理员时使用的数据传输对象")
public class AdminCreateDTO {

    @NotBlank(message = "管理员登录账号不能为空")
    @Schema(description = "管理员登录账号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "管理员姓名不能为空")
    @Schema(description = "管理员真实姓名或职位", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    // --- 新增字段 ---
    @NotNull(message = "必须为新管理员分配一个角色")
    @Schema(description = "要分配给新管理员的角色ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long roleId;
}