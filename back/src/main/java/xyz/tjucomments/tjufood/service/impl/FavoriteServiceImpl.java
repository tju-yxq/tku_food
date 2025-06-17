// src/main/java/xyz/tjucomments/tjufood/service/impl/FavoriteServiceImpl.java
package xyz.tjucomments.tjufood.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Favorite;
import xyz.tjucomments.tjufood.interceptor.UserHolder;
import xyz.tjucomments.tjufood.mapper.FavoriteMapper;
import xyz.tjucomments.tjufood.service.IBlogService;
import xyz.tjucomments.tjufood.service.IFavoriteService;
import xyz.tjucomments.tjufood.service.IStallService; // 【新增】注入窗口服务
import xyz.tjucomments.tjufood.utils.constants.IdPrefixConstants;
import xyz.tjucomments.tjufood.utils.constants.SystemConstants;
import xyz.tjucomments.tjufood.utils.id.RedisIdWorker;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements IFavoriteService {

    @Resource
    private RedisIdWorker redisIdWorker;
    @Resource
    private IBlogService blogService;
    @Resource
    private IStallService stallService; // 【新增】注入窗口服务

    @Override
    public Result toggleFavorite(Favorite favorite) {
        Long userId = UserHolder.getUser().getId();
        favorite.setUserId(userId);

        QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("favorite_id", favorite.getFavoriteId());
        queryWrapper.eq("type", favorite.getType());
        Favorite existingFavorite = getOne(queryWrapper);

        if (existingFavorite != null) {
            removeById(existingFavorite.getId());
            return Result.ok("取消收藏成功");
        } else {
            favorite.setId(redisIdWorker.nextId(IdPrefixConstants.FAVORITE_ID_PREFIX));
            save(favorite);
            return Result.ok("收藏成功");
        }
    }

    @Override
    public Result queryMyFavorites(Integer type, Integer current) {
        Long userId = UserHolder.getUser().getId();

        Page<Favorite> page = page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE),
                new QueryWrapper<Favorite>().eq("user_id", userId).eq("type", type));

        List<Favorite> records = page.getRecords();
        if (records.isEmpty()) {
            // 【修正】当记录为空时，返回一个空的列表和总数0
            return Result.ok(Collections.emptyList(), 0L);
        }

        List<Long> targetIds = records.stream().map(Favorite::getFavoriteId).collect(Collectors.toList());

        // 【修正】将 contentList 的类型声明为 List<?>
        List<?> contentList;
        if (type == 1) { // 收藏的是博客
            contentList = blogService.listByIds(targetIds);
        } else if (type == 2) { // 收藏的是窗口
            // 【修正】实现查询窗口的逻辑
            contentList = stallService.listByIds(targetIds);
        } else {
            return Result.fail("不支持的收藏类型");
        }

        return Result.ok(contentList, page.getTotal());
    }
}