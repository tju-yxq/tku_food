// src/main/java/xyz/tjucomments/tjufood/controller/BlogController.java
// (与上一轮回复中提供的代码一致，此处为完整性再次列出)
package xyz.tjucomments.tjufood.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import xyz.tjucomments.tjufood.aop.Log;
import xyz.tjucomments.tjufood.dto.BlogDTO;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.service.IBlogService;

@Tag(name = "05. 博客功能", description = "用户发布、查询、互动博客的接口")
@RestController
@RequestMapping("/api/blogs")
public class BlogController {

    @Resource
    private IBlogService blogService;

    @Operation(summary = "发布新博客")
    @SecurityRequirement(name = "authorization")
    @PostMapping
    @Log(module = "博客模块", operation = "发布博客")
    public Result createBlog(@RequestBody BlogDTO blogDTO) {
        return blogService.createBlog(blogDTO);
    }

    @Operation(summary = "根据ID查询博客详情")
    @GetMapping("/{id}")
    public Result getBlogById(@Parameter(description = "博客唯一标识ID") @PathVariable("id") Long id) {
        return blogService.queryBlogById(id);
    }

    @Operation(summary = "分页查询热门博客")
    @GetMapping("/hot")
    public Result queryHotBlogs(@Parameter(description = "当前页码") @RequestParam(value = "current", defaultValue = "1") Integer current) {
        return blogService.queryHotBlogs(current);
    }

    @Operation(summary = "点赞或取消点赞博客")
    @SecurityRequirement(name = "authorization")
    @PutMapping("/like/{id}")
    @Log(module = "博客模块", operation = "点赞博客")
    public Result likeBlog(@Parameter(description = "博客唯一标识ID") @PathVariable("id") Long id) {
        return blogService.likeBlog(id);
    }

    @Operation(summary = "查询为博客点赞的用户列表")
    @GetMapping("/likes/{id}")
    public Result queryBlogLikes(@Parameter(description = "博客唯一标识ID") @PathVariable("id") Long id) {
        return blogService.queryBlogLikes(id);
    }
}