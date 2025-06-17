// src/main/java/xyz/tjucomments/tjufood/controller/CommentController.java
package xyz.tjucomments.tjufood.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import xyz.tjucomments.tjufood.aop.Log;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Comment;
import xyz.tjucomments.tjufood.service.ICommentService;

@Tag(name = "07. 评论功能", description = "对博客等内容进行评论的接口")
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Resource
    private ICommentService commentService;

    @Operation(summary = "发表新评论")
    @SecurityRequirement(name = "authorization")
    @PostMapping
    @Log(module = "评论模块", operation = "发表评论")
    public Result addComment(@RequestBody Comment comment) {
        return commentService.addComment(comment);
    }

    @Operation(summary = "查询指定目标的评论列表")
    @GetMapping("/target/{targetId}")
    public Result listCommentsByTarget(
            @Parameter(description = "被评论对象的唯一ID") @PathVariable Long targetId,
            @Parameter(description = "被评论对象类型 (1=博客)") @RequestParam Integer type) {
        return commentService.queryCommentsByTarget(targetId, type);
    }
}