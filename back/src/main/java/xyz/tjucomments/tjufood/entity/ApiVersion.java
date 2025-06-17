package xyz.tjucomments.tjufood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * API版本管理实体类
 */
@Schema(description = "API版本管理实体对象")
@Data
@TableName("tb_api_version")
public class ApiVersion {

    @Schema(description = "版本ID", accessMode = Schema.AccessMode.READ_ONLY)
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @Schema(description = "版本号")
    private String versionNumber;

    @Schema(description = "版本名称")
    private String versionName;

    @Schema(description = "发布日期")
    private LocalDateTime releaseDate;

    @Schema(description = "状态 (开发中, 测试中, 已发布, 已废弃)")
    private String status;

    @Schema(description = "版本说明")
    private String description;

    @Schema(description = "更新日志")
    private String changelog;

    @Schema(description = "使用次数")
    private Long usageCount;

    @Schema(description = "是否当前版本 (0=否, 1=是)")
    private Integer isCurrent;

    @Schema(description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updateTime;
}
