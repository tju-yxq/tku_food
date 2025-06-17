// src/main/java/xyz/tjucomments/tjufood/controller/StallReviewController.java
package xyz.tjucomments.tjufood.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import xyz.tjucomments.tjufood.aop.Log;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.StallReview;
import xyz.tjucomments.tjufood.service.IStallReviewService;

@Tag(name = "06. 窗口评价功能", description = "用户提交、查询窗口评价的接口")
@RestController
@RequestMapping("/api/reviews") // 根路径设为/api/reviews
public class StallReviewController {

    @Resource
    private IStallReviewService stallReviewService;

    @Operation(summary = "提交窗口评价")
    @SecurityRequirement(name = "authorization")
    @PostMapping("/stall") // 使用子路径区分
    @Log(module = "评价模块", operation = "提交窗口评价")
    public Result addStallReview(@RequestBody StallReview review) {
        return stallReviewService.addReview(review);
    }

    @Operation(summary = "分页查询指定窗口的评价列表")
    @GetMapping("/stall/{stallId}")
    public Result listReviewsByStall(
            @Parameter(description = "窗口唯一标识ID") @PathVariable Long stallId,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer current) {
        return stallReviewService.queryReviewsByStallId(stallId, current);
    }
}