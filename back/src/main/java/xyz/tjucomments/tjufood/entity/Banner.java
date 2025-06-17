// 文件路径: src/main/java/xyz/tjucomments/tjufood/entity/Banner.java

package xyz.tjucomments.tjufood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "轮播图实体对象")
@Data
@TableName("tb_banner")
public class Banner {

    @Schema(description = "轮播图唯一ID", accessMode = Schema.AccessMode.READ_ONLY)
    // 基础数据，ID由数据库自增
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "图片的URL")
    private String imageUrl;

    @Schema(description = "点击后跳转的链接")
    private String linkUrl;

    @Schema(description = "排序值 (用于调整显示顺序)")
    private Integer sort;

    @Schema(description = "状态 (0=禁用, 1=启用)")
    private Integer status;

    @Schema(description = "展示开始时间 (用于定时上线)")
    private LocalDateTime startTime;

    @Schema(description = "展示结束时间 (用于定时下线)")
    private LocalDateTime endTime;

    @Schema(description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updateTime;
}