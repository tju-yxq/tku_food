package xyz.tjucomments.tjufood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema; // 新增导入
import lombok.Data;

/**
 * 管理员与角色的关联关系实体
 */
@Schema(description = "管理员与角色的关联关系实体") // 新增类级别注解
@Data
@TableName("tb_admin_role")
public class AdminRole {

    /**
     * 关联ID (主键)
     */
    @Schema(description = "关联关系唯一ID", accessMode = Schema.AccessMode.READ_ONLY) // 新增字段级别注解
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 管理员ID (外键)
     */
    @Schema(description = "关联的管理员ID", requiredMode = Schema.RequiredMode.REQUIRED) // 新增字段级别注解
    @TableField("admin_id")
    private Long adminId;

    /**
     * 角色ID (外键)
     */
    @Schema(description = "关联的角色ID", requiredMode = Schema.RequiredMode.REQUIRED) // 新增字段级别注解
    @TableField("role_id")
    private Long roleId;
}