// 文件路径: src/main/java/xyz/tjucomments/tjufood/entity/StallType.java

package xyz.tjucomments.tjufood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "窗口类型实体对象")
@Data
@TableName("tb_stall_type")
public class StallType {

    @Schema(description = "窗口类型唯一ID", accessMode = Schema.AccessMode.READ_ONLY)
    // 根据您的要求，基础数据使用数据库自增ID
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "类型名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "麻辣香锅")
    private String name;

    @Schema(description = "类型的图标URL", example = "http://example.com/icon_mlxg.png")
    private String icon;

    @Schema(description = "排序值 (用于调整显示顺序)", example = "1")
    private Integer sort;
}