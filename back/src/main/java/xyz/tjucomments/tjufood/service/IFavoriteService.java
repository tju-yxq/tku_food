// src/main/java/xyz/tjucomments/tjufood/service/IFavoriteService.java
package xyz.tjucomments.tjufood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Favorite;

public interface IFavoriteService extends IService<Favorite> {
    /**
     * 切换收藏状态
     * @param favorite 包含 targetId 和 type 的对象
     * @return 操作结果
     */
    Result toggleFavorite(Favorite favorite);

    /**
     * 分页查询我的收藏
     * @param type 收藏类型 (1=博客, 2=窗口)
     * @param current 当前页码
     * @return 收藏的内容列表
     */
    Result queryMyFavorites(Integer type, Integer current);
}