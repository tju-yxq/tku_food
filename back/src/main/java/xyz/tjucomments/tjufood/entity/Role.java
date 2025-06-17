// 文件路径: src/main/java/xyz/tjucomments/tjufood/entity/Role.java

package xyz.tjucomments.tjufood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "角色实体对象")
@Data
@TableName("tb_role")
public class Role {

    @Schema(description = "角色唯一ID", accessMode = Schema.AccessMode.READ_ONLY)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "角色名称", example = "内容管理员")
    private String name;

    @Schema(description = "角色描述", example = "负责管理食堂、窗口、菜品等核心基础数据")
    private String description;

    @Schema(description = "角色状态 (0=正常, 1=禁用)", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer status;

    @Schema(description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updateTime;
}