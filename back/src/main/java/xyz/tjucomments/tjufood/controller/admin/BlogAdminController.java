// 文件路径: src/main/java/xyz/tjucomments/tjufood/controller/admin/BlogAdminController.java

package xyz.tjucomments.tjufood.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Blog;
import xyz.tjucomments.tjufood.service.IBlogService;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "C02. 社区管理 - 博客管理", description = "对用户博客进行审核、管理等操作")
@SecurityRequirement(name = "authorization")
@RestController
@RequestMapping("/api/admin/blogs")
public class BlogAdminController {

    @Resource
    private IBlogService blogService;

    @Operation(summary = "分页查询博客列表", description = "支持按标题、用户ID、状态进行筛选")
    @GetMapping("/list")
    public Result listBlogs(
            @Parameter(description = "博客标题关键字") @RequestParam(required = false) String title,
            @Parameter(description = "作者用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "博客状态 (0=待审核, 1=通过, 2=拒绝)") @RequestParam(required = false) Integer status,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size
    ) {
        // 构建查询条件
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(title)) {
            queryWrapper.like(Blog::getTitle, title);
        }
        if (userId != null) {
            queryWrapper.eq(Blog::getUserId, userId);
        }
        if (status != null) {
            queryWrapper.eq(Blog::getStatus, status);
        }
        queryWrapper.orderByDesc(Blog::getCreateTime);

        // 执行分页查询
        Page<Blog> page = blogService.page(new Page<>(current, size), queryWrapper);

        // 返回分页结构
        Map<String, Object> result = new HashMap<>();
        result.put("records", page.getRecords());
        result.put("total", page.getTotal());
        return Result.ok(result);
    }

    @Operation(summary = "审核博客", description = "通过或拒绝一篇待审核的博客")
    @PutMapping("/{id}/audit")
    public Result auditBlog(
            @Parameter(description = "博客ID") @PathVariable Long id,
            @Parameter(description = "审核操作，包含新的状态 (1=通过, 2=拒绝)") @RequestBody Map<String, Integer> payload) {

        Integer newStatus = payload.get("status");
        if (newStatus == null || (newStatus != 1 && newStatus != 2)) {
            return Result.fail("无效的审核状态！");
        }

        Blog blog = new Blog();
        blog.setId(id);
        blog.setStatus(newStatus);
        boolean isSuccess = blogService.updateById(blog);

        return isSuccess ? Result.ok() : Result.fail("博客审核操作失败！");
    }

    @Operation(summary = "设置博客特殊状态", description = "设置或取消置顶、加精")
    @PutMapping("/{id}/specials")
    public Result setBlogSpecials(
            @Parameter(description = "博客ID") @PathVariable Long id,
            @Parameter(description = "包含isTop和isQuality的布尔值") @RequestBody Map<String, Boolean> specials) {

        Blog blog = new Blog();
        blog.setId(id);
        // 如果请求中包含isTop，则进行更新
        if (specials.containsKey("isTop")) {
            blog.setIsTop(specials.get("isTop") ? 1 : 0);
        }
        // 如果请求中包含isQuality，则进行更新
        if (specials.containsKey("isQuality")) {
            blog.setIsQuality(specials.get("isQuality") ? 1 : 0);
        }

        boolean isSuccess = blogService.updateById(blog);
        return isSuccess ? Result.ok() : Result.fail("设置失败！");
    }

    @Operation(summary = "管理员删除博客", description = "将博客状态设置为管理员删除，实现逻辑删除")
    @DeleteMapping("/{id}")
    public Result deleteBlogByAdmin(@Parameter(description = "博客ID") @PathVariable Long id) {
        Blog blog = new Blog();
        blog.setId(id);
        // 4 代表管理员删除状态
        blog.setStatus(4);
        boolean isSuccess = blogService.updateById(blog);
        return isSuccess ? Result.ok() : Result.fail("删除博客失败！");
    }
}