package xyz.tjucomments.tjufood.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Stall;
import xyz.tjucomments.tjufood.service.IStallService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Tag(name = "03. 窗口管理", description = "食堂窗口信息的增删改查接口")
@RestController
@RequestMapping("/api/stalls")
public class StallController {
    @Resource
    private IStallService stallService;

    @Operation(summary = "查询食堂下的所有窗口")
    @GetMapping("/canteen/{canteenId}")
    public Result getStallsByCanteenId(@Parameter(description = "所属食堂的ID", required = true) @PathVariable("canteenId") Long canteenId) {
        return stallService.listStallsByCanteenId(canteenId);
    }

    @Operation(summary = "查询单个窗口详情")
    @GetMapping("/{id}")
    public Result getStallById(@Parameter(description = "窗口的唯一ID", required = true) @PathVariable("id") Long id) {
        return stallService.queryStallById(id);
    }
}
