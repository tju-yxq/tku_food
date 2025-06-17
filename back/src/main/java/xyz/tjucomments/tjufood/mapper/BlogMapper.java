// src/main/java/xyz/tjucomments/tjufood/mapper/BlogMapper.java

package xyz.tjucomments.tjufood.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.tjucomments.tjufood.dto.BlogDTO;
import xyz.tjucomments.tjufood.entity.Blog;

@Mapper
public interface BlogMapper extends BaseMapper<Blog> {

    /**
     * 根据ID查询博客详情，并连接用户信息
     * @param id 博客ID
     * @return 包含作者信息的BlogDTO
     */
    BlogDTO queryBlogWithAuthor(@Param("id") Long id);
}