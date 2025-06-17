// 文件路径: src/main/java/xyz/tjucomments/tjufood/entity/SensitiveWord.java

package xyz.tjucomments.tjufood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "敏感词实体对象")
@Data
@TableName("tb_sensitive_word")
public class SensitiveWord {

    @Schema(description = "敏感词唯一ID", accessMode = Schema.AccessMode.READ_ONLY)
    // 【重要修正】根据您的要求，敏感词ID由程序生成
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @Schema(description = "敏感词内容")
    private String word;

    @Schema(description = "分类 (如: '广告', '政治')")
    private String category;

    @Schema(description = "敏感等级 (1-5, 数字越大越敏感)")
    private Integer level;

    @Schema(description = "状态 (0=禁用, 1=启用)")
    private Integer status;

    @Schema(description = "添加该词的管理员ID")
    private Long adminId;

    @Schema(description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updateTime;
}