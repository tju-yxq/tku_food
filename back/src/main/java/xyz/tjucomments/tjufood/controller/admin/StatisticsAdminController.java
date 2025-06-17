package xyz.tjucomments.tjufood.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import xyz.tjucomments.tjufood.entity.User;
import xyz.tjucomments.tjufood.entity.Blog;
import xyz.tjucomments.tjufood.entity.Canteen;
import xyz.tjucomments.tjufood.entity.Dish;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@Tag(name = "F01. 工具箱 - 统计面板", description = "提供系统数据统计功能")
@SecurityRequirement(name = "authorization")
@RestController
@RequestMapping("/api/admin/statistics")
public class StatisticsAdminController {

    @Resource
    private IUserService userService;
    
    @Resource
    private IBlogService blogService;
    
    @Resource
    private ICanteenService canteenService;
    
    @Resource
    private IDishService dishService;

    @Operation(summary = "获取系统概览统计")
    @GetMapping("/overview")
    public Result getOverviewStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 获取基础统计数据
        statistics.put("totalUsers", userService.count());
        statistics.put("totalBlogs", blogService.count());
        statistics.put("totalCanteens", canteenService.count());
        statistics.put("totalDishes", dishService.count());
        
        // 今日新增数据（真实查询）
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        long todayNewUsers = userService.count(
            new LambdaQueryWrapper<User>()
                .ge(User::getCreateTime, todayStart)
        );

        long todayNewBlogs = blogService.count(
            new LambdaQueryWrapper<Blog>()
                .ge(Blog::getCreateTime, todayStart)
        );

        statistics.put("todayNewUsers", todayNewUsers);
        statistics.put("todayNewBlogs", todayNewBlogs);
        statistics.put("todayComments", 45); // 评论功能暂时保持模拟数据
        
        return Result.ok(statistics);
    }

    @Operation(summary = "获取用户增长趋势")
    @GetMapping("/user-growth")
    public Result getUserGrowthTrend() {
        // 获取最近7天的真实数据
        List<String> dates = new ArrayList<>();
        List<Long> userCounts = new ArrayList<>();
        List<Long> blogCounts = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

            dates.add(date.toString());

            // 统计到该日期为止的累计用户数
            long userCount = userService.count(
                new LambdaQueryWrapper<User>()
                    .le(User::getCreateTime, dayEnd)
            );
            userCounts.add(userCount);

            // 统计到该日期为止的累计博客数
            long blogCount = blogService.count(
                new LambdaQueryWrapper<Blog>()
                    .le(Blog::getCreateTime, dayEnd)
            );
            blogCounts.add(blogCount);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("dates", dates.toArray(new String[0]));
        data.put("userCounts", userCounts.toArray(new Long[0]));
        data.put("blogCounts", blogCounts.toArray(new Long[0]));

        return Result.ok(data);
    }

    @Operation(summary = "获取热门菜品排行")
    @GetMapping("/popular-dishes")
    public Result getPopularDishes() {
        // 获取真实的菜品数据，按点赞数排序（因为没有score字段）
        List<Dish> topDishes = dishService.list(
            new LambdaQueryWrapper<Dish>()
                .orderByDesc(Dish::getLiked)
                .last("OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY")
        );

        List<Map<String, Object>> dishes = new ArrayList<>();
        for (int i = 0; i < topDishes.size(); i++) {
            Dish dish = topDishes.get(i);

            // 通过stallId获取食堂信息（需要先查询stall表再查询canteen表，这里简化处理）
            String canteenName = "未知食堂"; // 简化处理，因为需要通过stall表关联
            double score = dish.getLiked() != null ? dish.getLiked() / 10.0 : 0.0; // 将点赞数转换为评分

            dishes.add(createDishRank(i + 1, dish.getName(), canteenName, score));
        }

        // 如果没有数据，返回模拟数据
        if (dishes.isEmpty()) {
            dishes.add(createDishRank(1, "油泼面", "学一食堂", 4.9));
            dishes.add(createDishRank(2, "自选香锅", "学一食堂", 4.6));
            dishes.add(createDishRank(3, "黄焖鸡米饭", "学二食堂", 4.5));
            dishes.add(createDishRank(4, "牛肉拉面", "清真食堂", 4.5));
            dishes.add(createDishRank(5, "猪脚饭", "留学生食堂", 4.4));
        }

        return Result.ok(dishes);
    }

    @Operation(summary = "获取食堂分布统计")
    @GetMapping("/canteen-distribution")
    public Result getCanteenDistribution() {
        // 获取真实的食堂分布数据
        List<Canteen> canteens = canteenService.list();

        // 按校区统计食堂数量（Canteen实体没有campus字段，使用campusId）
        Map<String, Integer> campusCount = new HashMap<>();
        for (Canteen canteen : canteens) {
            String campus = "校区" + (canteen.getCampusId() != null ? canteen.getCampusId() : "未知");
            campusCount.put(campus, campusCount.getOrDefault(campus, 0) + 1);
        }

        // 转换为前端需要的格式
        List<Map<String, Object>> distribution = new ArrayList<>();
        String[] colors = {"#409EFF", "#67C23A", "#E6A23C", "#F56C6C", "#909399"};
        int colorIndex = 0;

        for (Map.Entry<String, Integer> entry : campusCount.entrySet()) {
            String color = colors[colorIndex % colors.length];
            distribution.add(createDistributionData(entry.getKey(), entry.getValue(), color));
            colorIndex++;
        }

        // 如果没有数据，返回模拟数据
        if (distribution.isEmpty()) {
            distribution.add(createDistributionData("北洋园校区", 8, "#409EFF"));
            distribution.add(createDistributionData("卫津路校区", 5, "#67C23A"));
            distribution.add(createDistributionData("其他校区", 2, "#E6A23C"));
        }

        return Result.ok(distribution);
    }

    private Map<String, Object> createDishRank(int rank, String name, String canteen, double score) {
        Map<String, Object> dish = new HashMap<>();
        dish.put("rank", rank);
        dish.put("name", name);
        dish.put("canteen", canteen);
        dish.put("score", score);
        return dish;
    }

    private Map<String, Object> createDistributionData(String name, int value, String color) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("value", value);
        data.put("itemStyle", Map.of("color", color));
        return data;
    }
}
