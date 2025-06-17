package xyz.tjucomments.tjufood.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.dto.UserDTO;
import xyz.tjucomments.tjufood.entity.Report;
import xyz.tjucomments.tjufood.mapper.ReportMapper;
import xyz.tjucomments.tjufood.service.IReportService;
import xyz.tjucomments.tjufood.interceptor.UserHolder;
import xyz.tjucomments.tjufood.utils.id.RedisIdWorker;

import jakarta.annotation.Resource;

@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements IReportService {

    @Resource
    private RedisIdWorker redisIdWorker;

    @Override
    @Transactional
    public Result submitReport(Report report) {
        UserDTO currentUser = UserHolder.getUser();
        if (currentUser == null) {
            return Result.fail("请先登录！");
        }
        
        report.setId(redisIdWorker.nextId("report"));
        report.setReporterId(currentUser.getId());
        report.setStatus(0); // 待处理

        boolean isSuccess = save(report);
        if (!isSuccess) {
            return Result.fail("举报提交失败！");
        }

        return Result.ok(report.getId());
    }
}
