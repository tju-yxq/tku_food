package xyz.tjucomments.tjufood.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import xyz.tjucomments.tjufood.aop.Log;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.service.IAuditLogService;

/**
 * 审计日志管理控制器
 * 包括操作日志和登录日志
 */
@Tag(name = "F04. 工具箱 - 审计日志", description = "审计日志查询和统计分析")
@SecurityRequirement(name = "authorization")
@RestController
@RequestMapping("/api/admin/audit-logs")
public class AuditLogController {

    @Resource
    private IAuditLogService auditLogService;

    @Operation(summary = "分页查询审计日志")
    @GetMapping("/list")
    @Log(module = "审计日志", operation = "查询审计日志")
    public Result getAuditLogs(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "日志类型 (1=操作日志, 2=登录日志, 3=安全日志)") @RequestParam(required = false) Integer logType,
            @Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endTime,
            @Parameter(description = "关键词搜索") @RequestParam(required = false) String keyword
    ) {
        return auditLogService.getAuditLogs(current, size, logType, startTime, endTime, keyword);
    }

    @Operation(summary = "获取审计统计信息")
    @GetMapping("/statistics")
    @Log(module = "审计日志", operation = "获取审计统计")
    public Result getAuditStatistics() {
        return auditLogService.getAuditStatistics();
    }

    @Operation(summary = "获取登录统计")
    @GetMapping("/login-stats")
    @Log(module = "审计日志", operation = "获取登录统计")
    public Result getLoginStatistics(
            @Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endTime
    ) {
        return auditLogService.getLoginStatistics(startTime, endTime);
    }

    @Operation(summary = "获取操作统计")
    @GetMapping("/operation-stats")
    @Log(module = "审计日志", operation = "获取操作统计")
    public Result getOperationStatistics(
            @Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endTime
    ) {
        return auditLogService.getOperationStatistics(startTime, endTime);
    }

    @Operation(summary = "获取风险事件统计")
    @GetMapping("/risk-stats")
    @Log(module = "审计日志", operation = "获取风险事件统计")
    public Result getRiskEventStatistics() {
        return auditLogService.getRiskEventStatistics();
    }

    @Operation(summary = "导出审计日志")
    @PostMapping("/export")
    @Log(module = "审计日志", operation = "导出审计日志")
    public Result exportAuditLogs(
            @Parameter(description = "日志类型") @RequestParam(required = false) Integer logType,
            @Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endTime
    ) {
        return auditLogService.exportAuditLogs(logType, startTime, endTime);
    }
}
