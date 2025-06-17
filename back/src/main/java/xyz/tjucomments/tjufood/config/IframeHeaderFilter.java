package xyz.tjucomments.tjufood.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * iframe响应头过滤器
 * 确保API文档可以在iframe中正常显示
 */
@Component
public class IframeHeaderFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // 移除可能阻止iframe嵌入的响应头
        httpResponse.setHeader("X-Frame-Options", "ALLOWALL");
        httpResponse.setHeader("Content-Security-Policy", "frame-ancestors *");
        
        // 添加CORS相关头部
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "*");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        
        chain.doFilter(request, response);
    }
}
