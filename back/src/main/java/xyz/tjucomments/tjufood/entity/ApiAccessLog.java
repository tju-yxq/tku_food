package xyz.tjucomments.tjufood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * API访问日志实体类
 * 记录所有RESTful API的访问情况
 */
@Schema(description = "API访问日志实体对象")
@Data
@TableName("tb_api_access_log")
public class ApiAccessLog {

    @Schema(description = "日志ID", accessMode = Schema.AccessMode.READ_ONLY)
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @Schema(description = "API路径")
    private String apiPath;

    @Schema(description = "HTTP方法")
    private String httpMethod;

    @Schema(description = "API名称/描述")
    private String apiName;

    @Schema(description = "API版本")
    private String apiVersion;

    @Schema(description = "调用者ID")
    private Long callerId;

    @Schema(description = "调用者类型 (1=管理员, 2=用户, 3=匿名)")
    private Integer callerType;

    @Schema(description = "调用者名称")
    private String callerName;

    @Schema(description = "请求参数")
    private String requestParams;

    @Schema(description = "请求头信息")
    private String requestHeaders;

    @Schema(description = "请求体大小(字节)")
    private Long requestSize;

    @Schema(description = "响应状态码")
    private Integer responseStatus;

    @Schema(description = "响应数据")
    private String responseData;

    @Schema(description = "响应体大小(字节)")
    private Long responseSize;

    @Schema(description = "响应时间(毫秒)")
    private Long responseTime;

    @Schema(description = "是否成功 (0=失败, 1=成功)")
    private Integer success;

    @Schema(description = "错误代码")
    private String errorCode;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "客户端IP")
    private String clientIp;

    @Schema(description = "IP归属地")
    private String ipLocation;

    @Schema(description = "用户代理")
    private String userAgent;

    @Schema(description = "请求来源")
    private String referer;

    @Schema(description = "请求ID/追踪ID")
    private String traceId;

    @Schema(description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createTime;
}
