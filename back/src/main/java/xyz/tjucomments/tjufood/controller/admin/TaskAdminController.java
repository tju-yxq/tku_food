package xyz.tjucomments.tjufood.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import xyz.tjucomments.tjufood.dto.Result;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Tag(name = "F02. 工具箱 - 任务管理", description = "系统任务管理功能")
@SecurityRequirement(name = "authorization")
@RestController
@RequestMapping("/api/admin/tasks")
public class TaskAdminController {

    // 模拟任务存储
    private static final List<Map<String, Object>> TASKS = new ArrayList<>();
    private static Long taskIdCounter = 1L;

    static {
        // 初始化一些示例任务
        TASKS.add(createTask(taskIdCounter++, "数据备份", "SCHEDULED", "HIGH", "每日凌晨2点执行数据库备份", "0 0 2 * * ?"));
        TASKS.add(createTask(taskIdCounter++, "日志清理", "RUNNING", "MEDIUM", "清理30天前的系统日志", "0 0 3 * * ?"));
        TASKS.add(createTask(taskIdCounter++, "缓存刷新", "COMPLETED", "LOW", "刷新系统缓存数据", "0 */30 * * * ?"));
    }

    @Operation(summary = "分页查询任务列表")
    @GetMapping("/list")
    public Result listTasks(
            @Parameter(description = "任务名称") @RequestParam(required = false) String taskName,
            @Parameter(description = "任务状态") @RequestParam(required = false) String status,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size
    ) {
        List<Map<String, Object>> filteredTasks = new ArrayList<>(TASKS);
        
        // 简单过滤
        if (taskName != null && !taskName.isEmpty()) {
            filteredTasks.removeIf(task -> !task.get("taskName").toString().contains(taskName));
        }
        if (status != null && !status.isEmpty()) {
            filteredTasks.removeIf(task -> !task.get("status").equals(status));
        }
        
        // 分页
        int start = (current - 1) * size;
        int end = Math.min(start + size, filteredTasks.size());
        List<Map<String, Object>> pageData = start < filteredTasks.size() ? 
            filteredTasks.subList(start, end) : new ArrayList<>();
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", pageData);
        result.put("total", filteredTasks.size());
        return Result.ok(result);
    }

    @Operation(summary = "创建新任务")
    @PostMapping
    public Result createTask(@RequestBody Map<String, Object> taskData) {
        Map<String, Object> newTask = createTask(
            taskIdCounter++,
            (String) taskData.get("taskName"),
            "SCHEDULED",
            (String) taskData.get("priority"),
            (String) taskData.get("description"),
            (String) taskData.get("cronExpression")
        );
        TASKS.add(newTask);
        return Result.ok(newTask.get("id"));
    }

    @Operation(summary = "更新任务")
    @PutMapping
    public Result updateTask(@RequestBody Map<String, Object> taskData) {
        Long id = Long.valueOf(taskData.get("id").toString());
        for (Map<String, Object> task : TASKS) {
            if (task.get("id").equals(id)) {
                task.put("taskName", taskData.get("taskName"));
                task.put("priority", taskData.get("priority"));
                task.put("description", taskData.get("description"));
                task.put("cronExpression", taskData.get("cronExpression"));
                task.put("updateTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                return Result.ok();
            }
        }
        return Result.fail("任务不存在！");
    }

    @Operation(summary = "删除任务")
    @DeleteMapping("/{id}")
    public Result deleteTask(@Parameter(description = "任务ID") @PathVariable Long id) {
        boolean removed = TASKS.removeIf(task -> task.get("id").equals(id));
        return removed ? Result.ok() : Result.fail("任务不存在！");
    }

    @Operation(summary = "执行任务")
    @PostMapping("/{id}/execute")
    public Result executeTask(@Parameter(description = "任务ID") @PathVariable Long id) {
        for (Map<String, Object> task : TASKS) {
            if (task.get("id").equals(id)) {
                task.put("status", "RUNNING");
                task.put("lastExecuteTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                
                // 模拟任务执行
                new Thread(() -> {
                    try {
                        Thread.sleep(3000); // 模拟执行3秒
                        task.put("status", "COMPLETED");
                    } catch (InterruptedException e) {
                        task.put("status", "FAILED");
                    }
                }).start();
                
                return Result.ok("任务开始执行");
            }
        }
        return Result.fail("任务不存在！");
    }

    @Operation(summary = "停止任务")
    @PostMapping("/{id}/stop")
    public Result stopTask(@Parameter(description = "任务ID") @PathVariable Long id) {
        for (Map<String, Object> task : TASKS) {
            if (task.get("id").equals(id)) {
                task.put("status", "STOPPED");
                return Result.ok("任务已停止");
            }
        }
        return Result.fail("任务不存在！");
    }

    private static Map<String, Object> createTask(Long id, String taskName, String status, String priority, String description, String cronExpression) {
        Map<String, Object> task = new HashMap<>();
        task.put("id", id);
        task.put("taskName", taskName);
        task.put("status", status);
        task.put("priority", priority);
        task.put("description", description);
        task.put("cronExpression", cronExpression);
        task.put("createTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        task.put("updateTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        task.put("lastExecuteTime", null);
        return task;
    }
}
