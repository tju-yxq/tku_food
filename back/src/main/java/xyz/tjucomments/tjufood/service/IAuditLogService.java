package xyz.tjucomments.tjufood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.AuditLog;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 审计日志服务接口
 */
public interface IAuditLogService extends IService<AuditLog> {
    
    /**
     * 记录操作日志
     */
    void saveOperationLog(AuditLog log);
    
    /**
     * 记录登录日志
     */
    void saveLoginLog(Long userId, Integer userType, String username, String ip, 
                     String userAgent, Integer status, String errorMsg);
    
    /**
     * 记录安全日志
     */
    void saveSecurityLog(String operation, String ip, String details, Integer riskLevel);
    
    /**
     * 分页查询审计日志
     */
    Result getAuditLogs(Integer current, Integer size, Integer logType, 
                       String startTime, String endTime, String keyword);
    
    /**
     * 获取审计统计信息
     */
    Result getAuditStatistics();
    
    /**
     * 获取登录统计
     */
    Result getLoginStatistics(String startTime, String endTime);
    
    /**
     * 获取操作统计
     */
    Result getOperationStatistics(String startTime, String endTime);
    
    /**
     * 获取风险事件统计
     */
    Result getRiskEventStatistics();
    
    /**
     * 导出审计日志
     */
    Result exportAuditLogs(Integer logType, String startTime, String endTime);
}
