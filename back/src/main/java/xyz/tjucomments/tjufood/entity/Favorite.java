// src/main/java/xyz/tjucomments/tjufood/entity/Favorite.java
package xyz.tjucomments.tjufood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "收藏记录实体")
@Data
@TableName("tb_favorite")
public class Favorite {

    @Schema(description = "收藏ID", accessMode = Schema.AccessMode.READ_ONLY)
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "被收藏对象ID")
    private Long favoriteId;

    @Schema(description = "被收藏对象类型 (1=博客, 2=窗口)")
    private Integer type;

    @Schema(description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createTime;
}