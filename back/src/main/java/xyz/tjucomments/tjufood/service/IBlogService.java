// src/main/java/xyz/tjucomments/tjufood/service/IBlogService.java
// (与上一轮回复中提供的代码一致，此处为完整性再次列出)
package xyz.tjucomments.tjufood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.tjucomments.tjufood.dto.BlogDTO;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Blog;

public interface IBlogService extends IService<Blog> {
    Result createBlog(BlogDTO blogDTO);
    Result queryBlogById(Long id);
    Result queryHotBlogs(Integer current);
    Result likeBlog(Long id);
    Result queryBlogLikes(Long id);
}