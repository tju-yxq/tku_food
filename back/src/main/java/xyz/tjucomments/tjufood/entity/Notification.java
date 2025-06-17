// src/main/java/xyz/tjucomments/tjufood/entity/Notification.java
package xyz.tjucomments.tjufood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "消息通知实体")
@Data
@TableName("tb_notification")
public class Notification {

    @Schema(description = "消息ID", accessMode = Schema.AccessMode.READ_ONLY)
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @Schema(description = "接收者ID")
    private Long recipientId;

    @Schema(description = "发送者ID (系统消息时为NULL)")
    private Long senderId;

    @Schema(description = "消息类型 (1=评论, 2=回复, 3=点赞, 4=系统)")
    private Integer type;

    @Schema(description = "关联对象ID (如博客ID)")
    private Long targetId;

    @Schema(description = "关联内容的摘要")
    private String targetContent;

    @Schema(description = "消息状态 (0=未读, 1=已读)")
    private Integer status;

    @Schema(description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createTime;
}