// src/main/java/xyz/tjucomments/tjufood/entity/UserVoucher.java
package xyz.tjucomments.tjufood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "用户优惠券关联实体")
@Data
@TableName("tb_user_voucher")
public class UserVoucher {

    @Schema(description = "主键ID", accessMode = Schema.AccessMode.READ_ONLY)
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "优惠券模板ID")
    private Long voucherId;

    @Schema(description = "状态 (0=未使用, 1=已使用, 2=已过期)")
    private Integer status;

    @Schema(description = "领取时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createTime;

    @Schema(description = "使用时间")
    private LocalDateTime useTime;
}