package xyz.tjucomments.tjufood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审计日志实体类
 * 包括操作日志和登录日志
 */
@Schema(description = "审计日志实体对象")
@Data
@TableName("tb_audit_log")
public class AuditLog {

    @Schema(description = "日志ID", accessMode = Schema.AccessMode.READ_ONLY)
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @Schema(description = "日志类型 (1=操作日志, 2=登录日志, 3=安全日志)")
    private Integer logType;

    @Schema(description = "操作人ID")
    private Long operatorId;

    @Schema(description = "操作人类型 (1=管理员, 2=用户)")
    private Integer operatorType;

    @Schema(description = "操作人名称")
    private String operatorName;

    @Schema(description = "业务模块")
    private String module;

    @Schema(description = "操作类型")
    private String operation;

    @Schema(description = "操作描述")
    private String description;

    @Schema(description = "请求方法")
    private String method;

    @Schema(description = "请求URL")
    private String url;

    @Schema(description = "请求参数")
    private String params;

    @Schema(description = "返回结果")
    private String result;

    @Schema(description = "操作状态 (0=失败, 1=成功)")
    private Integer status;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "执行时间(毫秒)")
    private Long executeTime;

    @Schema(description = "操作IP")
    private String ip;

    @Schema(description = "IP归属地")
    private String ipLocation;

    @Schema(description = "用户代理")
    private String userAgent;

    @Schema(description = "浏览器类型")
    private String browser;

    @Schema(description = "操作系统")
    private String os;

    @Schema(description = "设备类型")
    private String deviceType;

    @Schema(description = "风险等级 (1=低, 2=中, 3=高)")
    private Integer riskLevel;

    @Schema(description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createTime;
}
