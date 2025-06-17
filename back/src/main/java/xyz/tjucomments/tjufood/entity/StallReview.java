// src/main/java/xyz/tjucomments/tjufood/entity/StallReview.java

package xyz.tjucomments.tjufood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "窗口评价实体对象")
@Data
@TableName("tb_stall_review")
public class StallReview {

    @Schema(description = "评价ID", accessMode = Schema.AccessMode.READ_ONLY)
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @Schema(description = "评价者ID")
    private Long userId;

    @Schema(description = "被评价的窗口ID")
    private Long stallId;

    @Schema(description = "评价内容")
    private String content;

    @Schema(description = "本次消费人均价格")
    private BigDecimal costPerPerson;

    @Schema(description = "总体评分 (1-5星)")
    private Integer overallScore;

    @Schema(description = "口味评分 (1-5星)")
    private Integer tasteScore;

    @Schema(description = "环境评分 (1-5星)")
    private Integer environmentScore;

    @Schema(description = "服务评分 (1-5星)")
    private Integer serviceScore;

    @Schema(description = "性价比评分 (1-5星)")
    private Integer priceScore;

    @Schema(description = "评价被点赞的数量")
    private Integer liked;

    @Schema(description = "状态 (0=正常, 1=用户隐藏)")
    private Integer status;

    @Schema(description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updateTime;
}