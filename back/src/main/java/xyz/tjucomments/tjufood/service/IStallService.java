package xyz.tjucomments.tjufood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Stall;

public interface IStallService extends IService<Stall> {
    // 保留返回 DTO 的自定义方法
    Result queryStallById(Long id);
    Result listStallsByCanteenId(Long canteenId);
}