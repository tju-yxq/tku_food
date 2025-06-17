package xyz.tjucomments.tjufood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.ApiAccessLog;

/**
 * API访问日志服务接口
 */
public interface IApiAccessLogService extends IService<ApiAccessLog> {
    
    /**
     * 记录API访问日志
     */
    void saveApiAccessLog(ApiAccessLog log);
    
    /**
     * 分页查询API访问日志
     */
    Result getApiAccessLogs(Integer current, Integer size, String apiPath, 
                           String startTime, String endTime, Integer status);
    
    /**
     * 获取API统计信息
     */
    Result getApiStatistics();
    
    /**
     * 获取API性能统计
     */
    Result getApiPerformanceStats(String startTime, String endTime);
    
    /**
     * 获取API错误统计
     */
    Result getApiErrorStats(String startTime, String endTime);
    
    /**
     * 获取热门API排行
     */
    Result getPopularApis(Integer limit);
    
    /**
     * 获取慢查询API
     */
    Result getSlowApis(Integer limit, Long threshold);
    
    /**
     * 获取API调用趋势
     */
    Result getApiCallTrends(String startTime, String endTime, String granularity);
    
    /**
     * 获取API错误率统计
     */
    Result getApiErrorRates(String startTime, String endTime);
    
    /**
     * 导出API访问日志
     */
    Result exportApiAccessLogs(String startTime, String endTime, String apiPath);

    /**
     * 获取API版本信息
     */
    Result getApiVersions();
}
