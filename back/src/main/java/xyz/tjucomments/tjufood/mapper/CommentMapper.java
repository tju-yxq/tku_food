// src/main/java/xyz/tjucomments/tjufood/mapper/CommentMapper.java
package xyz.tjucomments.tjufood.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.tjucomments.tjufood.entity.Comment;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}