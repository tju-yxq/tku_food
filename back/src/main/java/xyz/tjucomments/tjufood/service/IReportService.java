package xyz.tjucomments.tjufood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Report;

public interface IReportService extends IService<Report> {
    
    /**
     * 提交举报
     * @param report 举报信息
     * @return 操作结果
     */
    Result submitReport(Report report);
}
