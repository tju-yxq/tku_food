package xyz.tjucomments.tjufood.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

/**
 * OpenAPI配置类
 * 用于自定义API文档的显示和排序
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TjuFood - 天津大学美食点评平台 API文档")
                        .description("为TjuFood项目提供在线API测试与文档服务")
                        .version("v1.2.0")
                        .contact(new Contact()
                                .name("喻新强")
                                .email("3023209091@tju.edu.cn")
                                .url("https://gitee.com/zjn79/TJUcomments")))
                .servers(Arrays.asList(
                        new Server().url("http://localhost:8090").description("本地开发环境"),
                        new Server().url("http://test-api.tjufood.com").description("测试环境"),
                        new Server().url("http://api.tjufood.com").description("生产环境")
                ));
    }

    @Bean
    public OpenApiCustomizer sortTagsAlphabetically() {
        return openApi -> {
            if (openApi.getTags() != null) {
                openApi.setTags(openApi.getTags()
                        .stream()
                        .sorted(Comparator.comparing(tag -> tag.getName()))
                        .collect(Collectors.toList()));
            }
            
            // 对路径进行排序
            if (openApi.getPaths() != null) {
                var sortedPaths = openApi.getPaths()
                        .entrySet()
                        .stream()
                        .sorted(Comparator.comparing(entry -> {
                            // 根据第一个tag进行排序
                            var operations = entry.getValue().readOperations();
                            if (!operations.isEmpty() && operations.get(0).getTags() != null && !operations.get(0).getTags().isEmpty()) {
                                return operations.get(0).getTags().get(0);
                            }
                            return "zzz"; // 没有tag的放到最后
                        }))
                        .collect(Collectors.toMap(
                                entry -> entry.getKey(),
                                entry -> entry.getValue(),
                                (e1, e2) -> e1,
                                LinkedHashMap::new));
                
                openApi.getPaths().clear();
                openApi.getPaths().putAll(sortedPaths);
            }
        };
    }
}
