package xyz.tjucomments.tjufood;

// 新增的import语句
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 【新增】@OpenAPIDefinition 注解，定义API文档基本信息
@OpenAPIDefinition(info = @Info(
        title = "TjuFood 项目 API 文档",
        description = "一个为天津大学学生提供食堂菜品点评与分享的平台 API",
        version = "v1.0.0"
))
// 【新增】@SecurityScheme 注解，定义一个名为 "authorization" 的安全方案
@SecurityScheme(
        name = "authorization", // 方案名称，与请求头中的字段名一致
        type = SecuritySchemeType.APIKEY, // 类型为APIKey
        in = SecuritySchemeIn.HEADER, // 认证信息在请求头(Header)中
        description = "用户或管理员登录后获取的Token"
)
@SpringBootApplication
public class TjuFoodApplication {

    public static void main(String[] args) {
        SpringApplication.run(TjuFoodApplication.class, args);
    }

}