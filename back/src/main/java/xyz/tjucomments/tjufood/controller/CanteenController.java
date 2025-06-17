package xyz.tjucomments.tjufood.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Canteen;
import xyz.tjucomments.tjufood.service.ICanteenService;
import xyz.tjucomments.tjufood.utils.constants.RedisConstants;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Tag(name = "02. 食堂管理", description = "食堂信息的增删改查接口")
@RestController
@RequestMapping("/api/canteens")
public class CanteenController {
    @Resource
    private ICanteenService canteenService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Operation(summary = "查询所有食堂列表")
    @GetMapping
    public Result listCanteens() {
        return canteenService.listCanteens();
    }

    @Operation(summary = "查询单个食堂详情")
    @GetMapping("/{id}")
    public Result getCanteenById(@Parameter(description = "食堂的唯一ID", required = true) @PathVariable("id") Long id) {
        return canteenService.queryCanteenById(id);
    }
}
