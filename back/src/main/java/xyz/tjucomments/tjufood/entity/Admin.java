package xyz.tjucomments.tjufood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;

@Schema(description = "管理员实体对象")
@Data
@TableName("tb_admin")
public class Admin {

    @Schema(description = "管理员唯一ID", accessMode = Schema.AccessMode.READ_ONLY)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "管理员登录账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin_content")
    private String username;

    @Schema(description = "管理员密码", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    @Schema(description = "管理员真实姓名或职位", example = "张三")
    private String name;

    @Schema(description = "所属角色ID")
    @TableField("role_id")
    private Long roleId;

    @Schema(description = "管理员状态 (0=正常, 1=禁用)", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer status;

    @Schema(description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updateTime;
}