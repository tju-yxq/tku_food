// 文件路径: src/main/java/xyz/tjucomments/tjufood/controller/admin/NoticeAdminController.java

package xyz.tjucomments.tjufood.controller.admin;

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
import xyz.tjucomments.tjufood.entity.Notice;
import xyz.tjucomments.tjufood.interceptor.AdminHolder;
import xyz.tjucomments.tjufood.service.INoticeService;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "D03. 运营管理 - 系统公告管理", description = "对系统公告进行增删改查")
@SecurityRequirement(name = "authorization")
@RestController
@RequestMapping("/api/admin/notices")
public class NoticeAdminController {

    @Resource
    private INoticeService noticeService;

    @Operation(summary = "新增或发布公告")
    @PostMapping
    public Result addNotice(@RequestBody Notice notice) {
        // ID由数据库自增
        // 设置发布公告的管理员ID
        AdminDTO currentAdmin = AdminHolder.getAdmin();
        if (currentAdmin != null) {
            // 直接从DTO中获取ID
            notice.setAdminId(currentAdmin.getId());
        }

        boolean isSuccess = noticeService.save(notice);
        return isSuccess ? Result.ok(notice.getId()) : Result.fail("发布公告失败！");
    }

    @Operation(summary = "删除公告")
    @DeleteMapping("/{id}")
    public Result deleteNotice(@Parameter(description = "公告ID") @PathVariable Long id) {
        boolean isSuccess = noticeService.removeById(id);
        return isSuccess ? Result.ok() : Result.fail("删除公告失败！");
    }

    @Operation(summary = "修改公告")
    @PutMapping
    public Result updateNotice(@RequestBody Notice notice) {
        if (notice.getId() == null) {
            return Result.fail("更新失败，ID不能为空");
        }
        boolean isSuccess = noticeService.updateById(notice);
        return isSuccess ? Result.ok() : Result.fail("更新公告失败！");
    }

    @Operation(summary = "分页查询公告列表")
    @GetMapping("/list")
    public Result listNotices(
            @Parameter(description = "公告标题关键字") @RequestParam(required = false) String title,
            @Parameter(description = "公告状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size
    ) {
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(title)) {
            queryWrapper.like("title", title);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("publish_time");

        Page<Notice> page = noticeService.page(new Page<>(current, size), queryWrapper);

        // 返回分页结构
        Map<String, Object> result = new HashMap<>();
        result.put("records", page.getRecords());
        result.put("total", page.getTotal());
        return Result.ok(result);
    }
}