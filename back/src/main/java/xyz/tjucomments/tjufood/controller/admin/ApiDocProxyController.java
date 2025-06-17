package xyz.tjucomments.tjufood.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * API文档代理控制器
 * 提供一个可以在iframe中正常显示的API文档页面
 */
@Tag(name = "API文档代理", description = "提供可嵌入的API文档页面")
@Controller
@RequestMapping("/api/admin/doc-proxy")
public class ApiDocProxyController {

    @Operation(summary = "获取可嵌入的API文档页面")
    @GetMapping("/embedded")
    public String getEmbeddedApiDoc() {
        // 返回一个自定义的API文档页面模板
        return "api-doc-embedded";
    }
}
