package xyz.tjucomments.tjufood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "校区实体对象")
@Data
@TableName("tb_campus")
public class Campus {

    @Schema(description = "校区唯一ID", accessMode = Schema.AccessMode.READ_ONLY)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "校区名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "卫津路校区")
    private String name;

    @Schema(description = "校区地址", example = "天津市南开区卫津路92号")
    private String address;
}