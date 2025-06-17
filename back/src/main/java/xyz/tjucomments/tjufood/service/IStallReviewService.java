// src/main/java/xyz/tjucomments/tjufood/service/IStallReviewService.java

package xyz.tjucomments.tjufood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.StallReview;

public interface IStallReviewService extends IService<StallReview> {

    /**
     * 新增窗口评价，并更新窗口的平均分
     * @param review 评价信息
     * @return 操作结果
     */
    Result addReview(StallReview review);

    /**
     * 根据窗口ID分页查询评价
     * @param stallId 窗口ID
     * @param current 当前页码
     * @return 评价列表
     */
    Result queryReviewsByStallId(Long stallId, Integer current);
}