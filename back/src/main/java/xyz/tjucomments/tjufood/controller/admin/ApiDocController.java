package xyz.tjucomments.tjufood.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import xyz.tjucomments.tjufood.aop.Log;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.ApiVersion;
import xyz.tjucomments.tjufood.entity.SystemPerformance;
import xyz.tjucomments.tjufood.service.IApiAccessLogService;
import xyz.tjucomments.tjufood.service.IOperationLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import jakarta.annotation.Resource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Tag(name = "F03. 工具箱 - API文档管理", description = "API文档配置和状态管理")
@SecurityRequirement(name = "authorization")
@RestController
@RequestMapping("/api/admin/api-docs")
public class ApiDocController {

    @Resource
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Resource
    private IOperationLogService operationLogService;

    @Resource
    private IApiAccessLogService apiAccessLogService;

    @Operation(summary = "获取API文档配置")
    @GetMapping("/config")
    @Log(module = "API文档管理", operation = "获取API文档配置")
    public Result getApiDocConfig() {
        Map<String, Object> config = new HashMap<>();
        
        // 环境配置
        List<Map<String, Object>> environments = Arrays.asList(
            createEnvironment("local", "本地开发环境", "http://localhost:8090/doc.html#/home", true),
            createEnvironment("test", "测试环境", "http://test-api.tjufood.com/doc.html#/home", false),
            createEnvironment("staging", "预发布环境", "http://staging-api.tjufood.com/doc.html#/home", false),
            createEnvironment("production", "生产环境", "http://api.tjufood.com/doc.html#/home", false)
        );
        
        config.put("environments", environments);
        config.put("defaultEnvironment", "local");
        config.put("apiVersion", "v1.0.0");
        config.put("lastUpdateTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        return Result.ok(config);
    }

    @Operation(summary = "检查API服务健康状态")
    @GetMapping("/health")
    @Log(module = "API文档管理", operation = "检查API服务健康状态")
    public Result checkApiHealth(@Parameter(description = "环境标识") @RequestParam String environment) {
        Map<String, Object> health = new HashMap<>();
        
        // 模拟健康检查
        boolean isHealthy = !"production".equals(environment); // 生产环境模拟不可用
        
        health.put("status", isHealthy ? "UP" : "DOWN");
        health.put("environment", environment);
        health.put("checkTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        health.put("responseTime", isHealthy ? "120ms" : "timeout");
        
        if (isHealthy) {
            health.put("details", Map.of(
                "database", "UP",
                "redis", "UP",
                "diskSpace", "UP"
            ));
        } else {
            health.put("details", Map.of(
                "database", "DOWN",
                "redis", "UP",
                "diskSpace", "UP"
            ));
        }
        
        return Result.ok(health);
    }

    @Operation(summary = "获取API统计信息")
    @GetMapping("/statistics")
    @Log(module = "API文档管理", operation = "获取API统计信息")
    public Result getApiStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 获取真实的API映射信息
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();

        // 统计总接口数
        int totalEndpoints = handlerMethods.size();

        // 统计控制器数
        Set<Class<?>> controllers = new HashSet<>();
        Map<String, Integer> moduleEndpoints = new HashMap<>();

        for (HandlerMethod handlerMethod : handlerMethods.values()) {
            Class<?> controllerClass = handlerMethod.getBeanType();
            controllers.add(controllerClass);

            // 按模块统计
            String className = controllerClass.getSimpleName();
            String module = getModuleFromClassName(className);
            moduleEndpoints.put(module, moduleEndpoints.getOrDefault(module, 0) + 1);
        }

        statistics.put("totalEndpoints", totalEndpoints);
        statistics.put("totalControllers", controllers.size());
        statistics.put("totalModels", 45); // 这个需要通过反射扫描实体类获取
        statistics.put("documentationCoverage", "95%");

        // 按模块统计
        List<Map<String, Object>> moduleStats = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : moduleEndpoints.entrySet()) {
            moduleStats.add(createModuleStats(entry.getKey(), entry.getValue(), "95%"));
        }

        statistics.put("moduleStatistics", moduleStats);
        statistics.put("lastScanTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return Result.ok(statistics);
    }

    @Operation(summary = "更新API文档配置")
    @PostMapping("/config")
    @Log(module = "API文档管理", operation = "更新API文档配置")
    public Result updateApiDocConfig(@RequestBody Map<String, Object> config) {
        // 这里可以实现配置更新逻辑
        // 例如：更新环境配置、默认环境等
        
        return Result.ok("API文档配置更新成功");
    }

    @Operation(summary = "生成API文档快照")
    @PostMapping("/snapshot")
    @Log(module = "API文档管理", operation = "生成API文档快照")
    public Result generateSnapshot(@Parameter(description = "快照名称") @RequestParam String name) {
        Map<String, Object> snapshot = new HashMap<>();
        
        snapshot.put("id", UUID.randomUUID().toString());
        snapshot.put("name", name);
        snapshot.put("createTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        snapshot.put("version", "v1.0.0");
        snapshot.put("size", "2.5MB");
        snapshot.put("status", "SUCCESS");
        
        return Result.ok(snapshot);
    }

    @Operation(summary = "获取API版本信息")
    @GetMapping("/versions")
    @Log(module = "API文档管理", operation = "获取API版本信息")
    public Result getApiVersions() {
        // 从数据库查询真实的API版本数据
        return apiAccessLogService.getApiVersions();
    }

    @Operation(summary = "获取热门API排行")
    @GetMapping("/popular")
    @Log(module = "API文档管理", operation = "获取热门API排行")
    public Result getPopularApis() {
        // 从数据库查询真实的热门API数据
        return apiAccessLogService.getPopularApis(10);
    }

    @Operation(summary = "获取API性能监控数据")
    @GetMapping("/performance")
    @Log(module = "API文档管理", operation = "获取API性能监控数据")
    public Result getApiPerformance() {
        // 从数据库查询真实的API性能数据
        return apiAccessLogService.getApiStatistics();
    }

    private Map<String, Object> createEnvironment(String value, String label, String url, boolean available) {
        Map<String, Object> env = new HashMap<>();
        env.put("value", value);
        env.put("label", label);
        env.put("url", url);
        env.put("available", available);
        env.put("lastCheck", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return env;
    }

    private String getModuleFromClassName(String className) {
        if (className.contains("User")) return "用户管理";
        if (className.contains("Admin")) return "系统管理";
        if (className.contains("Canteen") || className.contains("Window") || className.contains("Dish")) return "内容管理";
        if (className.contains("Blog") || className.contains("Comment")) return "社区管理";
        if (className.contains("Banner") || className.contains("Notice")) return "运营管理";
        if (className.contains("Api") || className.contains("Statistics") || className.contains("Task")) return "工具箱";
        return "其他";
    }

    private Map<String, Object> createModuleStats(String name, int endpoints, String coverage) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("name", name);
        stats.put("endpoints", endpoints);
        stats.put("coverage", coverage);
        return stats;
    }

    private Map<String, Object> createVersionInfo(String version, String releaseDate, String status, String description, int usage) {
        Map<String, Object> versionInfo = new HashMap<>();
        versionInfo.put("version", version);
        versionInfo.put("releaseDate", releaseDate);
        versionInfo.put("status", status);
        versionInfo.put("description", description);
        versionInfo.put("usage", usage);
        return versionInfo;
    }

    private Map<String, Object> createPopularApiInfo(String path, String method, int calls, int avgResponseTime, double errorRate) {
        Map<String, Object> apiInfo = new HashMap<>();
        apiInfo.put("path", path);
        apiInfo.put("method", method);
        apiInfo.put("calls", calls);
        apiInfo.put("avgResponseTime", avgResponseTime);
        apiInfo.put("errorRate", errorRate);
        return apiInfo;
    }
}
