package xyz.tjucomments.tjufood.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.OperationLog;
import xyz.tjucomments.tjufood.service.IOperationLogService;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "D05. 运营管理 - 日志管理", description = "查看系统操作日志")
@SecurityRequirement(name = "authorization")
@RestController
@RequestMapping("/api/admin/logs")
public class LogAdminController {

    @Resource
    private IOperationLogService operationLogService;

    @Operation(summary = "分页查询操作日志")
    @GetMapping("/list")
    public Result listLogs(
            @Parameter(description = "操作人名称") @RequestParam(required = false) String operatorName,
            @Parameter(description = "操作模块") @RequestParam(required = false) String module,
            @Parameter(description = "操作类型") @RequestParam(required = false) String operation,
            @Parameter(description = "操作状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endTime,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size
    ) {
        QueryWrapper<OperationLog> queryWrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(operatorName)) {
            queryWrapper.like("operator_name", operatorName);
        }
        if (StringUtils.hasText(module)) {
            queryWrapper.like("module", module);
        }
        if (StringUtils.hasText(operation)) {
            queryWrapper.like("operation", operation);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        
        // 时间范围查询
        if (StringUtils.hasText(startTime)) {
            try {
                LocalDateTime start = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                queryWrapper.ge("create_time", start);
            } catch (Exception e) {
                // 忽略时间格式错误
            }
        }
        if (StringUtils.hasText(endTime)) {
            try {
                LocalDateTime end = LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                queryWrapper.le("create_time", end);
            } catch (Exception e) {
                // 忽略时间格式错误
            }
        }
        
        queryWrapper.orderByDesc("create_time");
        
        Page<OperationLog> page = operationLogService.page(new Page<>(current, size), queryWrapper);
        
        // 返回分页结构
        Map<String, Object> result = new HashMap<>();
        result.put("records", page.getRecords());
        result.put("total", page.getTotal());
        return Result.ok(result);
    }

    @Operation(summary = "查询日志详情")
    @GetMapping("/{id}")
    public Result getLogDetail(@Parameter(description = "日志ID") @PathVariable Long id) {
        OperationLog log = operationLogService.getById(id);
        return log != null ? Result.ok(log) : Result.fail("日志记录不存在！");
    }

    @Operation(summary = "删除日志记录")
    @DeleteMapping("/{id}")
    public Result deleteLog(@Parameter(description = "日志ID") @PathVariable Long id) {
        boolean isSuccess = operationLogService.removeById(id);
        return isSuccess ? Result.ok() : Result.fail("删除日志记录失败！");
    }

    @Operation(summary = "批量删除日志记录")
    @DeleteMapping("/batch")
    public Result batchDeleteLogs(@RequestBody Map<String, Object> params) {
        // 实现批量删除逻辑
        return Result.ok("批量删除功能待实现");
    }

    @Operation(summary = "清理过期日志")
    @DeleteMapping("/cleanup")
    public Result cleanupLogs(@Parameter(description = "保留天数") @RequestParam(defaultValue = "30") Integer days) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(days);
        QueryWrapper<OperationLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.lt("create_time", cutoffTime);
        
        boolean isSuccess = operationLogService.remove(queryWrapper);
        return isSuccess ? Result.ok("清理完成") : Result.fail("清理失败");
    }
}
