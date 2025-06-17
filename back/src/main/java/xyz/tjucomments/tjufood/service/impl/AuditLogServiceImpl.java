package xyz.tjucomments.tjufood.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.AuditLog;
import xyz.tjucomments.tjufood.mapper.AuditLogMapper;
import xyz.tjucomments.tjufood.service.IAuditLogService;
import xyz.tjucomments.tjufood.utils.id.RedisIdWorker;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * 审计日志服务实现类
 */
@Slf4j
@Service
public class AuditLogServiceImpl extends ServiceImpl<AuditLogMapper, AuditLog> implements IAuditLogService {

    @Resource
    private RedisIdWorker redisIdWorker;

    @Override
    @Async
    public void saveOperationLog(AuditLog auditLog) {
        try {
            auditLog.setId(redisIdWorker.nextId("audit_log"));
            auditLog.setLogType(1); // 操作日志
            auditLog.setCreateTime(LocalDateTime.now());
            save(auditLog);
        } catch (Exception e) {
            log.error("保存操作日志失败", e);
        }
    }

    @Override
    @Async
    public void saveLoginLog(Long userId, Integer userType, String username, String ip, 
                            String userAgent, Integer status, String errorMsg) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setId(redisIdWorker.nextId("audit_log"));
            auditLog.setLogType(2); // 登录日志
            auditLog.setOperatorId(userId);
            auditLog.setOperatorType(userType);
            auditLog.setOperatorName(username);
            auditLog.setOperation(status == 1 ? "登录成功" : "登录失败");
            auditLog.setDescription(status == 1 ? "用户登录成功" : "用户登录失败：" + errorMsg);
            auditLog.setStatus(status);
            auditLog.setErrorMsg(errorMsg);
            auditLog.setIp(ip);
            auditLog.setUserAgent(userAgent);
            auditLog.setCreateTime(LocalDateTime.now());
            save(auditLog);
        } catch (Exception e) {
            log.error("保存登录日志失败", e);
        }
    }

    @Override
    @Async
    public void saveSecurityLog(String operation, String ip, String details, Integer riskLevel) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setId(redisIdWorker.nextId("audit_log"));
            auditLog.setLogType(3); // 安全日志
            auditLog.setOperation(operation);
            auditLog.setDescription(details);
            auditLog.setIp(ip);
            auditLog.setRiskLevel(riskLevel);
            auditLog.setCreateTime(LocalDateTime.now());
            save(auditLog);
        } catch (Exception e) {
            log.error("保存安全日志失败", e);
        }
    }

    @Override
    public Result getAuditLogs(Integer current, Integer size, Integer logType,
                              String startTime, String endTime, String keyword) {
        // 创建分页对象
        Page<AuditLog> page = new Page<>(current, size);

        // 构建查询条件
        IPage<AuditLog> logPage = lambdaQuery()
                .eq(logType != null, AuditLog::getLogType, logType)
                .like(keyword != null, AuditLog::getDescription, keyword)
                .like(keyword != null, AuditLog::getOperatorName, keyword)
                .like(keyword != null, AuditLog::getModule, keyword)
                .ge(startTime != null, AuditLog::getCreateTime, startTime)
                .le(endTime != null, AuditLog::getCreateTime, endTime)
                .orderByDesc(AuditLog::getCreateTime)
                .page(page);

        // 返回分页结果
        Map<String, Object> result = new HashMap<>();
        result.put("records", logPage.getRecords());
        result.put("total", logPage.getTotal());

        return Result.ok(result);
    }

    @Override
    public Result getAuditStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 获取真实统计数据
        long totalLogs = count();

        // 今日登录数量
        long todayLogins = lambdaQuery()
                .eq(AuditLog::getLogType, 2) // 登录日志
                .ge(AuditLog::getCreateTime, LocalDateTime.now().toLocalDate())
                .count();

        // 今日操作数量
        long todayOperations = lambdaQuery()
                .eq(AuditLog::getLogType, 1) // 操作日志
                .ge(AuditLog::getCreateTime, LocalDateTime.now().toLocalDate())
                .count();

        // 风险事件数量
        long riskEvents = lambdaQuery()
                .eq(AuditLog::getLogType, 3) // 安全日志
                .or()
                .ge(AuditLog::getRiskLevel, 2) // 中高风险
                .count();

        statistics.put("totalLogs", totalLogs);
        statistics.put("todayLogins", todayLogins);
        statistics.put("todayOperations", todayOperations);
        statistics.put("riskEvents", riskEvents);

        return Result.ok(statistics);
    }

    @Override
    public Result getLoginStatistics(String startTime, String endTime) {
        Map<String, Object> statistics = new HashMap<>();

        // 登录成功次数
        long successLogins = lambdaQuery()
                .eq(AuditLog::getLogType, 2) // 登录日志
                .eq(AuditLog::getStatus, 1) // 成功
                .ge(startTime != null, AuditLog::getCreateTime, startTime)
                .le(endTime != null, AuditLog::getCreateTime, endTime)
                .count();

        // 登录失败次数
        long failedLogins = lambdaQuery()
                .eq(AuditLog::getLogType, 2) // 登录日志
                .eq(AuditLog::getStatus, 0) // 失败
                .ge(startTime != null, AuditLog::getCreateTime, startTime)
                .le(endTime != null, AuditLog::getCreateTime, endTime)
                .count();

        // 独立用户数（去重）
        List<AuditLog> loginLogs = lambdaQuery()
                .eq(AuditLog::getLogType, 2)
                .ge(startTime != null, AuditLog::getCreateTime, startTime)
                .le(endTime != null, AuditLog::getCreateTime, endTime)
                .isNotNull(AuditLog::getOperatorId)
                .list();

        long uniqueUsers = loginLogs.stream()
                .map(AuditLog::getOperatorId)
                .distinct()
                .count();

        statistics.put("successLogins", successLogins);
        statistics.put("failedLogins", failedLogins);
        statistics.put("uniqueUsers", uniqueUsers);
        statistics.put("successRate", successLogins + failedLogins > 0 ?
            (double) successLogins / (successLogins + failedLogins) * 100 : 0);

        return Result.ok(statistics);
    }

    @Override
    public Result getOperationStatistics(String startTime, String endTime) {
        Map<String, Object> statistics = new HashMap<>();

        // 按模块统计操作次数
        List<AuditLog> operationLogs = lambdaQuery()
                .eq(AuditLog::getLogType, 1) // 操作日志
                .ge(startTime != null, AuditLog::getCreateTime, startTime)
                .le(endTime != null, AuditLog::getCreateTime, endTime)
                .list();

        Map<String, Long> moduleStats = operationLogs.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    log -> log.getModule() != null ? log.getModule() : "未知模块",
                    java.util.stream.Collectors.counting()
                ));

        // 按操作类型统计
        Map<String, Long> operationStats = operationLogs.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    log -> log.getOperation() != null ? log.getOperation() : "未知操作",
                    java.util.stream.Collectors.counting()
                ));

        // 成功失败统计
        long successOperations = operationLogs.stream()
                .mapToLong(log -> log.getStatus() == 1 ? 1 : 0)
                .sum();

        long failedOperations = operationLogs.size() - successOperations;

        statistics.put("moduleStats", moduleStats);
        statistics.put("operationStats", operationStats);
        statistics.put("successOperations", successOperations);
        statistics.put("failedOperations", failedOperations);
        statistics.put("totalOperations", operationLogs.size());

        return Result.ok(statistics);
    }

    @Override
    public Result getRiskEventStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 按风险等级统计
        long lowRisk = lambdaQuery()
                .eq(AuditLog::getRiskLevel, 1)
                .count();

        long mediumRisk = lambdaQuery()
                .eq(AuditLog::getRiskLevel, 2)
                .count();

        long highRisk = lambdaQuery()
                .eq(AuditLog::getRiskLevel, 3)
                .count();

        // 安全日志统计
        long securityEvents = lambdaQuery()
                .eq(AuditLog::getLogType, 3) // 安全日志
                .count();

        // 失败操作统计
        long failedOperations = lambdaQuery()
                .eq(AuditLog::getStatus, 0) // 失败
                .count();

        // 最近的高风险事件
        List<AuditLog> recentHighRiskEvents = lambdaQuery()
                .eq(AuditLog::getRiskLevel, 3)
                .orderByDesc(AuditLog::getCreateTime)
                .last("OFFSET 0 ROWS FETCH NEXT 5 ROWS ONLY")
                .list();

        statistics.put("lowRisk", lowRisk);
        statistics.put("mediumRisk", mediumRisk);
        statistics.put("highRisk", highRisk);
        statistics.put("securityEvents", securityEvents);
        statistics.put("failedOperations", failedOperations);
        statistics.put("recentHighRiskEvents", recentHighRiskEvents);

        return Result.ok(statistics);
    }

    @Override
    public Result exportAuditLogs(Integer logType, String startTime, String endTime) {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "导出功能开发中");
        return Result.ok(result);
    }
}
