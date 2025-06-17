// 文件路径: src/main/java/xyz/tjucomments/tjufood/controller/admin/SensitiveWordAdminController.java

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
import xyz.tjucomments.tjufood.dto.AdminDTO;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Admin;
import xyz.tjucomments.tjufood.entity.SensitiveWord;
import xyz.tjucomments.tjufood.interceptor.AdminHolder;
import xyz.tjucomments.tjufood.service.ISensitiveWordService;
import xyz.tjucomments.tjufood.utils.id.RedisIdWorker; // 【新增】导入ID生成器

import java.util.HashMap;
import java.util.Map;

@Tag(name = "C03. 社区管理 - 敏感词管理", description = "对平台敏感词进行增删改查")
@SecurityRequirement(name = "authorization")
@RestController
@RequestMapping("/api/admin/sensitive-words")
public class SensitiveWordAdminController {

    @Resource
    private ISensitiveWordService sensitiveWordService;

    @Resource
    private RedisIdWorker redisIdWorker; // 【新增】注入ID生成器

    @Operation(summary = "新增敏感词")
    @PostMapping
    public Result addSensitiveWord(@RequestBody SensitiveWord sensitiveWord) {
        // 【重要修正】在保存前，手动生成并设置ID
        long wordId = redisIdWorker.nextId("sensitive_word");
        sensitiveWord.setId(wordId);

        // 【修正】AdminHolder中存放的是AdminDTO类型
        AdminDTO currentAdmin = AdminHolder.getAdmin();
        if (currentAdmin != null) {
            // 直接从DTO中获取ID
            sensitiveWord.setAdminId(currentAdmin.getId());
        }

        boolean isSuccess = sensitiveWordService.save(sensitiveWord);
        return isSuccess ? Result.ok(sensitiveWord.getId()) : Result.fail("新增敏感词失败！");
    }

    @Operation(summary = "删除敏感词")
    @DeleteMapping("/{id}")
    public Result deleteSensitiveWord(@Parameter(description = "敏感词ID") @PathVariable Long id) {
        boolean isSuccess = sensitiveWordService.removeById(id);
        return isSuccess ? Result.ok() : Result.fail("删除敏感词失败！");
    }

    @Operation(summary = "修改敏感词")
    @PutMapping
    public Result updateSensitiveWord(@RequestBody SensitiveWord sensitiveWord) {
        if (sensitiveWord.getId() == null) {
            return Result.fail("更新失败，ID不能为空");
        }
        boolean isSuccess = sensitiveWordService.updateById(sensitiveWord);
        return isSuccess ? Result.ok() : Result.fail("更新敏感词失败！");
    }

    @Operation(summary = "分页查询敏感词列表")
    @GetMapping("/list")
    public Result listSensitiveWords(
            @Parameter(description = "敏感词内容关键词") @RequestParam(required = false) String word,
            @Parameter(description = "敏感词分类") @RequestParam(required = false) String category,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size
    ) {
        LambdaQueryWrapper<SensitiveWord> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(word)) {
            queryWrapper.like(SensitiveWord::getWord, word);
        }
        if (StringUtils.hasText(category)) {
            queryWrapper.eq(SensitiveWord::getCategory, category);
        }
        queryWrapper.orderByDesc(SensitiveWord::getCreateTime);

        Page<SensitiveWord> page = sensitiveWordService.page(new Page<>(current, size), queryWrapper);

        // 返回分页结构
        Map<String, Object> result = new HashMap<>();
        result.put("records", page.getRecords());
        result.put("total", page.getTotal());
        return Result.ok(result);
    }
}