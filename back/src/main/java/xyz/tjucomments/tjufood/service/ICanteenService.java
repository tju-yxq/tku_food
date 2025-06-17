package xyz.tjucomments.tjufood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Canteen;

// 【重要】我们不再需要自定义分页方法，所以只保留您原有的方法
public interface ICanteenService extends IService<Canteen> {

    Result listCanteens();

    Result queryCanteenById(Long id);
}