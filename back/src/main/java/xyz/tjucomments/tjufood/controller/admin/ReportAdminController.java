package xyz.tjucomments.tjufood.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import xyz.tjucomments.tjufood.dto.AdminDTO;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Report;
import xyz.tjucomments.tjufood.service.IReportService;
import xyz.tjucomments.tjufood.interceptor.AdminHolder;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "C04. 社区管理 - 举报管理", description = "对用户举报进行处理")
@SecurityRequirement(name = "authorization")
@RestController
@RequestMapping("/api/admin/reports")
public class ReportAdminController {

    @Resource
    private IReportService reportService;

    @Operation(summary = "分页查询举报列表")
    @GetMapping("/list")
    public Result listReports(
            @Parameter(description = "举报人ID") @RequestParam(required = false) Long reporterId,
            @Parameter(description = "被举报对象类型") @RequestParam(required = false) Integer targetType,
            @Parameter(description = "举报状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size
    ) {
        QueryWrapper<Report> queryWrapper = new QueryWrapper<>();
        
        if (reporterId != null) {
            queryWrapper.eq("reporter_id", reporterId);
        }
        if (targetType != null) {
            queryWrapper.eq("target_type", targetType);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        
        queryWrapper.orderByDesc("create_time");
        
        Page<Report> page = reportService.page(new Page<>(current, size), queryWrapper);
        
        // 返回分页结构
        Map<String, Object> result = new HashMap<>();
        result.put("records", page.getRecords());
        result.put("total", page.getTotal());
        return Result.ok(result);
    }

    @Operation(summary = "处理举报")
    @PutMapping("/{id}/handle")
    public Result handleReport(
            @Parameter(description = "举报ID") @PathVariable Long id,
            @RequestBody Map<String, Object> handleData
    ) {
        Report report = reportService.getById(id);
        if (report == null) {
            return Result.fail("举报记录不存在！");
        }
        
        if (report.getStatus() != 0) {
            return Result.fail("该举报已被处理！");
        }
        
        AdminDTO currentAdmin = AdminHolder.getAdmin();
        if (currentAdmin == null) {
            return Result.fail("请先登录！");
        }
        
        Integer newStatus = (Integer) handleData.get("status");
        String result = (String) handleData.get("result");
        
        if (newStatus == null || (newStatus != 1 && newStatus != 2)) {
            return Result.fail("处理状态无效！");
        }
        
        report.setStatus(newStatus);
        report.setResult(result);
        report.setHandlerId(currentAdmin.getId());
        report.setHandleTime(LocalDateTime.now());
        
        boolean isSuccess = reportService.updateById(report);
        return isSuccess ? Result.ok() : Result.fail("处理举报失败！");
    }

    @Operation(summary = "删除举报记录")
    @DeleteMapping("/{id}")
    public Result deleteReport(@Parameter(description = "举报ID") @PathVariable Long id) {
        boolean isSuccess = reportService.removeById(id);
        return isSuccess ? Result.ok() : Result.fail("删除举报记录失败！");
    }

    @Operation(summary = "批量处理举报")
    @PutMapping("/batch-handle")
    public Result batchHandleReports(@RequestBody Map<String, Object> batchData) {
        // 实现批量处理逻辑
        return Result.ok("批量处理功能待实现");
    }
}
