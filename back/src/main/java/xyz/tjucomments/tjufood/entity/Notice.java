// 文件路径: src/main/java/xyz/tjucomments/tjufood/entity/Notice.java

package xyz.tjucomments.tjufood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "系统公告实体对象")
@Data
@TableName("tb_notice")
public class Notice {

    @Schema(description = "公告唯一ID", accessMode = Schema.AccessMode.READ_ONLY)
    // 基础数据，ID由数据库自增
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "发布公告的管理员ID")
    private Long adminId;

    @Schema(description = "公告标题")
    private String title;

    @Schema(description = "公告内容 (支持富文本)")
    private String content;

    @Schema(description = "公告类型 (0=普通, 1=重要)")
    private Integer type;

    @Schema(description = "状态 (0=草稿, 1=已发布, 2=已撤回)")
    private Integer status;

    @Schema(description = "预定发布时间 (用于定时发布)")
    private LocalDateTime publishTime;

    @Schema(description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updateTime;
}