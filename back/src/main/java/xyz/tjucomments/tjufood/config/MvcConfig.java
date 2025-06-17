package xyz.tjucomments.tjufood.config;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry; // 新增导入
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import xyz.tjucomments.tjufood.interceptor.AdminLoginInterceptor;
import xyz.tjucomments.tjufood.interceptor.AdminRefreshTokenInterceptor;
import xyz.tjucomments.tjufood.interceptor.ApiAccessLogInterceptor;
import xyz.tjucomments.tjufood.interceptor.LoginInterceptor;
import xyz.tjucomments.tjufood.interceptor.RefreshTokenInterceptor;
/**
 * @author Gemini
 * @description Spring MVC 配置，主要用于注册拦截器
 * @create 2025-06-16 14:15
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private RefreshTokenInterceptor refreshTokenInterceptor;
    @Resource
    private LoginInterceptor loginInterceptor;
    @Resource
    private AdminRefreshTokenInterceptor adminRefreshTokenInterceptor;

    @Resource
    private ApiAccessLogInterceptor apiAccessLogInterceptor;
    @Resource
    private AdminLoginInterceptor adminLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // ======================== 0. API访问日志拦截器 ========================
        // 0.1 API访问日志记录拦截器 (order=-1, 最先执行)
        // 作用: 记录所有API的访问情况，用于监控和分析
        registry.addInterceptor(apiAccessLogInterceptor)
                .addPathPatterns("/api/**")
                .order(-1);

        // ======================== 1. 前台用户拦截器配置 ========================
        // 1.1 Token刷新拦截器 (order=0, 最先执行)
        // 作用: 拦截所有请求，检查并刷新用户Token，将UserDTO存入ThreadLocal。
        // 不做任何拦截，只负责刷新。
        registry.addInterceptor(refreshTokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns( // 排除后台和API文档相关路径
                        "/api/admin/**",
                        "/doc.html",
                        "/doc.html/**",
                        "/webjars/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/favicon.ico",
                        "/error"
                )
                .order(0);

        // 1.2 登录校验拦截器 (order=1)
        // 作用: 拦截需要登录才能访问的用户接口。
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/users/**")
                .excludePathPatterns( // 明确排除登录、注册、重置密码等公共接口
                        "/api/users/login",
                        "/api/users/register",
                        "/api/users/password"
                        // 注意: /api/users/logout 需要登录，因此不在此处排除
                )
                .order(1);


        // ======================== 2. 后台管理员拦截器配置 ========================
        // 2.1 管理员Token刷新拦截器 (order=2)
        // 作用: 拦截所有后台请求，检查并刷新管理员Token，将AdminDTO存入ThreadLocal。
        registry.addInterceptor(adminRefreshTokenInterceptor)
                .addPathPatterns("/api/admin/**")
                .order(2);

        // 2.2 管理员登录校验拦截器 (order=3)
        // 作用: 拦截需要登录才能访问的后台接口。
        registry.addInterceptor(adminLoginInterceptor)
                .addPathPatterns("/api/admin/**")
                // 【高优先级修复】必须排除管理员登录接口，否则无法登录
                .excludePathPatterns("/api/admin/login")
                .order(3);
    }
    // 【新增】注入配置文件中的上传路径
    @Value("${tjufood.image-upload-dir}")
    private String uploadDir;



    /**
     * 【新增】静态资源映射
     * 用于在开发环境下，浏览器能直接访问上传到磁盘的图片
     * @param registry a {@link org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry} object
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/imgs/**") // 浏览器访问路径，例如 /imgs/xxx.jpg
                .addResourceLocations("file:" + uploadDir); // 映射到本地磁盘的绝对路径
    }
}