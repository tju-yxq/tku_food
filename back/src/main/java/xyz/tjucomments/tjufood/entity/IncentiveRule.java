// 文件路径: src/main/java/xyz/tjucomments/tjufood/entity/IncentiveRule.java

package xyz.tjucomments.tjufood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户激励规则实体对象")
@Data
@TableName("tb_incentive_rule")
public class IncentiveRule {

    @Schema(description = "规则唯一ID", accessMode = Schema.AccessMode.READ_ONLY)
    // 基础数据，ID由数据库自增
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "行为类型 (如: 'DAILY_SIGN_IN', 'POST_BLOG')，程序中使用的唯一标识")
    private String actionType;

    @Schema(description = "完成该行为奖励的积分")
    private Integer credits;

    @Schema(description = "每日可通过该行为获取积分的次数上限")
    private Integer dailyLimit;

    @Schema(description = "规则描述")
    private String description;

    @Schema(description = "规则状态 (0=禁用, 1=启用)")
    private Integer status;

    @Schema(description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updateTime;
}