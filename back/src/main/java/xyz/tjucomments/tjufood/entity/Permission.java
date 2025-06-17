// 文件路径: src/main/java/xyz/tjucomments/tjufood/entity/Permission.java

package xyz.tjucomments.tjufood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "权限实体对象")
@Data
@TableName("tb_permission")
public class Permission {

    @Schema(description = "权限唯一ID", accessMode = Schema.AccessMode.READ_ONLY)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "权限标识符", example = "content:canteen:create")
    private String name;

    @Schema(description = "权限的详细描述", example = "创建食堂")
    private String description;

    @Schema(description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createTime;
}