package xyz.tjucomments.tjufood.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.tjucomments.tjufood.dto.DishDTO;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Dish;
import xyz.tjucomments.tjufood.mapper.DishMapper;
import xyz.tjucomments.tjufood.service.IDishService;
import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements IDishService {

    @Override
    public Result queryDishById(Long id) {
        // XML中的自定义多表查询返回DishDTO，保持不变
        DishDTO dish = baseMapper.queryDishById(id);
        if(dish == null) {
            return Result.fail("菜品不存在！");
        }
        return Result.ok(dish);
    }

    @Override
    public Result listDishesByStallId(Long stallId) {
        // XML中的自定义多表查询返回DishDTO，保持不变
        List<DishDTO> dishes = baseMapper.listDishesByStallId(stallId);
        return Result.ok(dishes);
    }

    @Override
    public Result searchDishesByName(String name, Integer current, Integer size) {
        // 保持原有方法不变，调用新的重载方法
        return searchDishesByName(name, current, size, "score", "desc");
    }

    @Override
    public Result searchDishesByName(String name, Integer current, Integer size, String sortBy, String sortOrder) {
        // XML中的自定义多表查询返回DishDTO，支持自定义排序
        Page<DishDTO> page = new Page<>(current, size);
        List<DishDTO> dishes = baseMapper.searchDishesByNameWithSort(page, name, sortBy, sortOrder);
        return Result.ok(dishes, page.getTotal());
    }

    // addDish, updateDish, deleteDish 等冗余方法已移除。
    // Controller层将直接调用由IService提供的 save, updateById, removeById。
}