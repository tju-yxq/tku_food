// src/main/java/xyz/tjucomments/tjufood/entity/CreditRecord.java
package xyz.tjucomments.tjufood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "积分流水记录实体")
@Data
@TableName("tb_credit_record")
public class CreditRecord {

    @Schema(description = "记录ID", accessMode = Schema.AccessMode.READ_ONLY)
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "积分获取类型/行为")
    private String actionType;

    @Schema(description = "变动积分 (正数表示增加, 负数表示减少)")
    private Integer credits;

    @Schema(description = "变动描述")
    private String description;

    @Schema(description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createTime;
}