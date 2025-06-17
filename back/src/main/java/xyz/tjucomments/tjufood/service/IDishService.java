package xyz.tjucomments.tjufood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Dish;

public interface IDishService extends IService<Dish> {
    // 保留返回 DTO 或包含特殊业务逻辑的方法
    Result queryDishById(Long id);
    Result listDishesByStallId(Long stallId);
    Result searchDishesByName(String name, Integer current, Integer size);
    Result searchDishesByName(String name, Integer current, Integer size, String sortBy, String sortOrder);

    // addDish, updateDish, deleteDish 等方法已由 IService 的标准方法
    // save, updateById, removeById 替代，故此处无需定义。
}