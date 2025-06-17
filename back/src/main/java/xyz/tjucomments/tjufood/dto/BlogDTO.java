// src/main/java/xyz/tjucomments/tjufood/dto/BlogDTO.java

package xyz.tjucomments.tjufood.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.tjucomments.tjufood.entity.Blog;

@Data
@EqualsAndHashCode(callSuper = true)
public class BlogDTO extends Blog {
    /**
     * 作者昵称
     */
    private String authorName;
    /**
     * 作者头像
     */
    private String authorIcon;
    /**
     * 当前登录用户是否已点赞
     */
    private Boolean isLiked;
}