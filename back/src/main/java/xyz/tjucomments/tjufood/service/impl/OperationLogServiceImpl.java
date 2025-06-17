package xyz.tjucomments.tjufood.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.OperationLog;
import xyz.tjucomments.tjufood.mapper.OperationLogMapper;
import xyz.tjucomments.tjufood.service.IOperationLogService;
import xyz.tjucomments.tjufood.utils.id.RedisIdWorker;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Resource;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements IOperationLogService {

    @Resource
    private RedisIdWorker redisIdWorker;

    @Override
    public void saveLog(OperationLog log) {
        log.setId(redisIdWorker.nextId("operation_log"));
        save(log);
    }

    @Override
    public Result getOperationLogs(Integer current, Integer size) {
        // 创建分页对象
        Page<OperationLog> page = new Page<>(current, size);

        // 查询分页数据，按创建时间倒序
        IPage<OperationLog> logPage = lambdaQuery()
                .orderByDesc(OperationLog::getCreateTime)
                .page(page);

        // 返回分页结果
        Map<String, Object> result = new HashMap<>();
        result.put("records", logPage.getRecords());
        result.put("total", logPage.getTotal());

        return Result.ok(result);
    }
}
