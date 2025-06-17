package xyz.tjucomments.tjufood.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import xyz.tjucomments.tjufood.entity.ApiAccessLog;
import xyz.tjucomments.tjufood.dto.UserDTO;
import xyz.tjucomments.tjufood.service.IApiAccessLogService;
import xyz.tjucomments.tjufood.interceptor.UserHolder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * API访问日志拦截器
 * 自动记录所有API的访问情况
 */
@Slf4j
@Component
public class ApiAccessLogInterceptor implements HandlerInterceptor {

    @Resource
    private IApiAccessLogService apiAccessLogService;

    private static final String START_TIME_ATTR = "startTime";
    private static final String TRACE_ID_ATTR = "traceId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 记录请求开始时间
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());
        
        // 生成追踪ID
        String traceId = "trace-" + UUID.randomUUID().toString().substring(0, 8);
        request.setAttribute(TRACE_ID_ATTR, traceId);
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        try {
            // 只记录API请求（以/api开头的路径）
            String requestURI = request.getRequestURI();
            if (!requestURI.startsWith("/api")) {
                return;
            }

            // 计算响应时间
            Long startTime = (Long) request.getAttribute(START_TIME_ATTR);
            long responseTime = startTime != null ? System.currentTimeMillis() - startTime : 0;

            // 获取追踪ID
            String traceId = (String) request.getAttribute(TRACE_ID_ATTR);

            // 创建API访问日志
            ApiAccessLog accessLog = new ApiAccessLog();
            accessLog.setApiPath(requestURI);
            accessLog.setHttpMethod(request.getMethod());
            accessLog.setResponseTime(responseTime);
            accessLog.setResponseStatus(response.getStatus());
            accessLog.setSuccess(response.getStatus() >= 200 && response.getStatus() < 400 ? 1 : 0);
            accessLog.setClientIp(getClientIp(request));
            accessLog.setUserAgent(request.getHeader("User-Agent"));
            accessLog.setReferer(request.getHeader("Referer"));
            accessLog.setTraceId(traceId);

            // 获取当前用户信息
            UserDTO currentUser = UserHolder.getUser();
            if (currentUser != null) {
                accessLog.setCallerId(currentUser.getId());
                accessLog.setCallerType(2); // 普通用户
                accessLog.setCallerName(currentUser.getNickName());
            } else {
                // 检查是否是管理员请求
                if (requestURI.startsWith("/api/admin")) {
                    accessLog.setCallerType(1); // 管理员
                    accessLog.setCallerName("admin"); // 可以从session或token中获取
                } else {
                    accessLog.setCallerType(3); // 匿名用户
                    accessLog.setCallerName("匿名用户");
                }
            }

            // 设置API名称（可以从注解中获取，这里简化处理）
            accessLog.setApiName(getApiName(requestURI, request.getMethod()));

            // 记录请求参数（简化处理，避免记录敏感信息）
            String params = getRequestParams(request);
            if (StrUtil.isNotBlank(params) && params.length() < 1000) {
                accessLog.setRequestParams(params);
            }

            // 异步保存日志
            apiAccessLogService.saveApiAccessLog(accessLog);

        } catch (Exception e) {
            log.error("记录API访问日志失败", e);
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 获取API名称
     */
    private String getApiName(String uri, String method) {
        // 简化的API名称映射
        if (uri.contains("/login")) return "用户登录";
        if (uri.contains("/register")) return "用户注册";
        if (uri.contains("/user/list")) return "查询用户列表";
        if (uri.contains("/canteen")) return "食堂管理";
        if (uri.contains("/dish")) return "菜品管理";
        if (uri.contains("/blog")) return "博客管理";
        if (uri.contains("/comment")) return "评论管理";
        if (uri.contains("/statistics")) return "统计分析";
        if (uri.contains("/upload")) return "文件上传";
        
        return method + " " + uri;
    }

    /**
     * 获取请求参数
     */
    private String getRequestParams(HttpServletRequest request) {
        try {
            if ("GET".equals(request.getMethod())) {
                return request.getQueryString();
            } else {
                // 对于POST等请求，这里简化处理
                // 实际项目中可以通过RequestWrapper来获取请求体
                return "POST/PUT请求参数";
            }
        } catch (Exception e) {
            return "参数获取失败";
        }
    }
}
