package xyz.tjucomments.tjufood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "操作日志实体对象")
@Data
@TableName("tb_log")
public class OperationLog {

    @Schema(description = "日志ID", accessMode = Schema.AccessMode.READ_ONLY)
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @Schema(description = "操作的管理员ID")
    private Long adminId;

    @Schema(description = "操作描述")
    private String operation;

    @Schema(description = "请求的方法名")
    private String method;

    @Schema(description = "请求参数 (JSON)")
    private String params;

    @Schema(description = "操作者IP")
    private String ip;

    @Schema(description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createTime;
}
