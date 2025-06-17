package xyz.tjucomments.tjufood.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import xyz.tjucomments.tjufood.aop.Log;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.service.IApiAccessLogService;

/**
 * API管理控制器
 * 包括RESTful API的访问日志和错误日志管理
 */
@Tag(name = "F05. 工具箱 - API管理", description = "RESTful API访问监控和错误分析")
@SecurityRequirement(name = "authorization")
@RestController
@RequestMapping("/api/admin/api-management")
public class ApiManagementController {

    @Resource
    private IApiAccessLogService apiAccessLogService;

    @Operation(summary = "分页查询API访问日志")
    @GetMapping("/access-logs")
    @Log(module = "API管理", operation = "查询API访问日志")
    public Result getApiAccessLogs(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "API路径") @RequestParam(required = false) String apiPath,
            @Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endTime,
            @Parameter(description = "状态码") @RequestParam(required = false) Integer status
    ) {
        return apiAccessLogService.getApiAccessLogs(current, size, apiPath, startTime, endTime, status);
    }

    @Operation(summary = "获取API统计信息")
    @GetMapping("/statistics")
    @Log(module = "API管理", operation = "获取API统计")
    public Result getApiStatistics() {
        return apiAccessLogService.getApiStatistics();
    }

    @Operation(summary = "获取API性能统计")
    @GetMapping("/performance-stats")
    @Log(module = "API管理", operation = "获取API性能统计")
    public Result getApiPerformanceStats(
            @Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endTime
    ) {
        return apiAccessLogService.getApiPerformanceStats(startTime, endTime);
    }

    @Operation(summary = "获取API错误统计")
    @GetMapping("/error-stats")
    @Log(module = "API管理", operation = "获取API错误统计")
    public Result getApiErrorStats(
            @Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endTime
    ) {
        return apiAccessLogService.getApiErrorStats(startTime, endTime);
    }

    @Operation(summary = "获取热门API排行")
    @GetMapping("/popular-apis")
    @Log(module = "API管理", operation = "获取热门API排行")
    public Result getPopularApis(
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") Integer limit
    ) {
        return apiAccessLogService.getPopularApis(limit);
    }

    @Operation(summary = "获取慢查询API")
    @GetMapping("/slow-apis")
    @Log(module = "API管理", operation = "获取慢查询API")
    public Result getSlowApis(
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") Integer limit,
            @Parameter(description = "响应时间阈值(毫秒)") @RequestParam(defaultValue = "1000") Long threshold
    ) {
        return apiAccessLogService.getSlowApis(limit, threshold);
    }

    @Operation(summary = "获取API调用趋势")
    @GetMapping("/call-trends")
    @Log(module = "API管理", operation = "获取API调用趋势")
    public Result getApiCallTrends(
            @Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endTime,
            @Parameter(description = "时间粒度 (hour/day/month)") @RequestParam(defaultValue = "hour") String granularity
    ) {
        return apiAccessLogService.getApiCallTrends(startTime, endTime, granularity);
    }

    @Operation(summary = "获取API错误率统计")
    @GetMapping("/error-rates")
    @Log(module = "API管理", operation = "获取API错误率统计")
    public Result getApiErrorRates(
            @Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endTime
    ) {
        return apiAccessLogService.getApiErrorRates(startTime, endTime);
    }

    @Operation(summary = "导出API访问日志")
    @PostMapping("/export")
    @Log(module = "API管理", operation = "导出API访问日志")
    public Result exportApiAccessLogs(
            @Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endTime,
            @Parameter(description = "API路径") @RequestParam(required = false) String apiPath
    ) {
        return apiAccessLogService.exportApiAccessLogs(startTime, endTime, apiPath);
    }
}
