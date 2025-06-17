// src/main/java/xyz/tjucomments/tjufood/service/ICommentService.java
package xyz.tjucomments.tjufood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Comment;

public interface ICommentService extends IService<Comment> {
    Result addComment(Comment comment);
    Result queryCommentsByTarget(Long targetId, Integer type);
}