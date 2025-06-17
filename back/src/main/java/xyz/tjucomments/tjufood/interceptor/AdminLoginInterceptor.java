package xyz.tjucomments.tjufood.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 后台登录校验拦截器
 * 作用: 在需要登录的后台接口执行前，检查当前管理员是否已登录。
 */
@Component
public class AdminLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 从AdminHolder中尝试获取管理员信息
        if (AdminHolder.getAdmin() == null) {
            // 2. 如果获取不到，说明管理员未登录，拦截并返回401
            response.setStatus(401);
            return false;
        }
        // 3. 如果有管理员信息，放行
        return true;
    }
}