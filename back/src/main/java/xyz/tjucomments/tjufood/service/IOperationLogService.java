package xyz.tjucomments.tjufood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.OperationLog;

public interface IOperationLogService extends IService<OperationLog> {

    /**
     * 记录操作日志
     * @param log 日志信息
     */
    void saveLog(OperationLog log);

    /**
     * 分页获取操作日志
     * @param current 当前页
     * @param size 每页大小
     * @return 分页结果
     */
    Result getOperationLogs(Integer current, Integer size);
}
