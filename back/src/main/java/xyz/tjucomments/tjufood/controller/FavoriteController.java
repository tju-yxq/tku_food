// src/main/java/xyz/tjucomments/tjufood/controller/FavoriteController.java
package xyz.tjucomments.tjufood.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import xyz.tjucomments.tjufood.aop.Log;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Favorite;
import xyz.tjucomments.tjufood.service.IFavoriteService;

@Tag(name = "09. 收藏功能", description = "用户收藏博客、窗口等内容的接口")
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Resource
    private IFavoriteService favoriteService;

    @Operation(summary = "收藏或取消收藏", description = "这是一个开关接口，如果未收藏则收藏，已收藏则取消")
    @SecurityRequirement(name = "authorization")
    @PostMapping
    @Log(module = "收藏模块", operation = "切换收藏状态")
    public Result toggleFavorite(@RequestBody Favorite favorite) {
        return favoriteService.toggleFavorite(favorite);
    }

    @Operation(summary = "查询我的收藏列表")
    @SecurityRequirement(name = "authorization")
    @GetMapping("/my")
    public Result listMyFavorites(
            @Parameter(description = "收藏类型 (1=博客, 2=窗口)") @RequestParam("type") Integer type,
            @Parameter(description = "当前页码") @RequestParam(value = "current", defaultValue = "1") Integer current) {
        return favoriteService.queryMyFavorites(type, current);
    }
}