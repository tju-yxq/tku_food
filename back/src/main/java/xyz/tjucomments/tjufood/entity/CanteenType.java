package xyz.tjucomments.tjufood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "食堂类型实体对象")
@Data
@TableName("tb_canteen_type")
public class CanteenType {

    @Schema(description = "食堂类型唯一ID", accessMode = Schema.AccessMode.READ_ONLY)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "类型名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "学生食堂")
    private String name;

    @Schema(description = "类型的图标URL", example = "http://example.com/icon.png")
    private String icon;

    @Schema(description = "排序值 (用于调整显示顺序)", example = "1")
    private Integer sort;
}