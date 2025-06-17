// src/main/java/xyz/tjucomments/tjufood/entity/Comment.java
package xyz.tjucomments.tjufood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "评论实体对象")
@Data
@TableName("tb_comment")
public class Comment {

    @Schema(description = "评论ID", accessMode = Schema.AccessMode.READ_ONLY)
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @Schema(description = "评论者ID")
    private Long userId;

    @Schema(description = "被评论对象ID (博客或评论ID)")
    private Long targetId;

    @Schema(description = "被评论对象类型 (1=博客, 2=评论)")
    private Integer targetType;

    @Schema(description = "父评论ID (用于楼中楼)")
    private Long parentId;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "被点赞的数量")
    private Integer liked;

    @Schema(description = "状态 (0=正常, 1=用户隐藏)")
    private Integer status;

    @Schema(description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createTime;
}