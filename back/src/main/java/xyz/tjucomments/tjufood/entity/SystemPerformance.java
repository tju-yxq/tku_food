package xyz.tjucomments.tjufood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 系统性能监控实体类
 */
@Schema(description = "系统性能监控实体对象")
@Data
@TableName("tb_system_performance")
public class SystemPerformance {

    @Schema(description = "性能ID", accessMode = Schema.AccessMode.READ_ONLY)
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @Schema(description = "指标名称")
    private String metricName;

    @Schema(description = "指标值")
    private BigDecimal metricValue;

    @Schema(description = "指标单位")
    private String metricUnit;

    @Schema(description = "指标类型 (CPU, Memory, Disk, Network, API)")
    private String metricType;

    @Schema(description = "记录时间")
    private LocalDateTime recordTime;

    @Schema(description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createTime;
}
