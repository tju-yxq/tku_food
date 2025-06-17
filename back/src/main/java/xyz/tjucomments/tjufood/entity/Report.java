package xyz.tjucomments.tjufood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "举报实体对象")
@Data
@TableName("tb_report")
public class Report {

    @Schema(description = "举报ID", accessMode = Schema.AccessMode.READ_ONLY)
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @Schema(description = "举报人用户ID")
    private Long reporterId;

    @Schema(description = "被举报对象ID")
    private Long targetId;

    @Schema(description = "被举报对象类型 (1=博客, 2=评论, 3=用户)")
    private Integer targetType;

    @Schema(description = "举报原因")
    private String reason;

    @Schema(description = "举报详细描述")
    private String description;

    @Schema(description = "举报状态 (0=待处理, 1=已处理, 2=已驳回)")
    private Integer status;

    @Schema(description = "处理结果")
    private String result;

    @Schema(description = "处理人管理员ID")
    private Long handlerId;

    @Schema(description = "处理时间")
    private LocalDateTime handleTime;

    @Schema(description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updateTime;
}
