package xyz.tjucomments.tjufood.aop;

import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import xyz.tjucomments.tjufood.dto.AdminDTO;
import xyz.tjucomments.tjufood.dto.UserDTO;
import xyz.tjucomments.tjufood.entity.AuditLog;
import xyz.tjucomments.tjufood.entity.OperationLog;
import xyz.tjucomments.tjufood.interceptor.AdminHolder;
import xyz.tjucomments.tjufood.interceptor.UserHolder;
import xyz.tjucomments.tjufood.service.IAuditLogService;
import xyz.tjucomments.tjufood.service.IOperationLogService;

import jakarta.annotation.Resource;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author Gemini
 * @description 操作日志记录AOP切面
 * @create 2025-06-16 14:15
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    @Resource
    private IOperationLogService operationLogService;

    @Resource
    private IAuditLogService auditLogService;

    /**
     * 定义切点，拦截所有被 @Log 注解标记的方法
     */
    @Pointcut("@annotation(xyz.tjucomments.tjufood.aop.Log)")
    public void logPointcut() {
    }

    /**
     * 环绕通知，在目标方法执行前后进行日志记录
     *
     * @param joinPoint 切点
     * @return 目标方法的返回值
     * @throws Throwable 异常
     */
    @Around("logPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Exception exception = null;

        try {
            // 执行目标方法
            result = joinPoint.proceed();
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            long timeTaken = System.currentTimeMillis() - startTime;

            // 异步记录日志，避免影响主流程性能
            try {
                handleLog(joinPoint, result, timeTaken, exception);
            } catch (Exception e) {
                log.error("AOP 日志记录异常", e);
            }
        }

        return result;
    }

    /**
     * 核心日志处理方法
     *
     * @param joinPoint 切点信息
     * @param result    方法执行结果
     * @param timeTaken 方法耗时
     * @param exception 异常信息
     */
    private void handleLog(ProceedingJoinPoint joinPoint, Object result, long timeTaken, Exception exception) {
        // 获取请求属性
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();

        // 获取方法上的@Log注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Log logAnnotation = method.getAnnotation(Log.class);

        // 构建日志信息
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("\n===================== API Log Start =====================");
        logBuilder.append("\n| 操作模块: ").append(logAnnotation.module());
        logBuilder.append("\n| 操作描述: ").append(logAnnotation.operation());
        logBuilder.append("\n| 请求方式: ").append(request.getMethod());
        logBuilder.append("\n| 请求URI: ").append(request.getRequestURI());
        logBuilder.append("\n| 请求IP: ").append(getClientIp(request));

        // 记录操作人
        String operator = getOperator();
        logBuilder.append("\n| 操作人员: ").append(operator);

        // 记录请求参数
        try {
            logBuilder.append("\n| 请求参数: ").append(JSON.toJSONString(joinPoint.getArgs()));
        } catch (Exception e) {
            logBuilder.append("\n| 请求参数: [参数序列化失败]");
        }

        // 记录响应结果
        try {
            logBuilder.append("\n| 响应结果: ").append(JSON.toJSONString(result));
        } catch (Exception e) {
            logBuilder.append("\n| 响应结果: [结果序列化失败]");
        }

        logBuilder.append("\n| 执行耗时: ").append(timeTaken).append(" ms");
        logBuilder.append("\n===================== API Log End =======================");

        // 使用info级别打印格式化的日志
        log.info(logBuilder.toString());

        // 保存到数据库
        saveToDatabase(joinPoint, result, timeTaken, exception, request, logAnnotation, operator);

        // 同时保存到审计日志
        saveToAuditLog(joinPoint, result, timeTaken, exception, request, logAnnotation);
    }

    /**
     * 获取当前操作员信息
     *
     * @return 操作员标识
     */
    private String getOperator() {
        AdminDTO admin = AdminHolder.getAdmin();
        if (admin != null) {
            return "Admin(ID:" + admin.getId() + ", User:" + admin.getUsername() + ")";
        }
        UserDTO user = UserHolder.getUser();
        if (user != null) {
            return "User(ID:" + user.getId() + ", Nick:" + user.getNickName() + ")";
        }
        return "Anonymous";
    }

    /**
     * 获取客户端IP地址
     *
     * @param request HttpServletRequest
     * @return IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 保存操作日志到数据库
     */
    private void saveToDatabase(ProceedingJoinPoint joinPoint, Object result, long timeTaken,
                               Exception exception, HttpServletRequest request, Log logAnnotation, String operator) {
        try {
            OperationLog operationLog = new OperationLog();

            // 设置操作人信息（只记录管理员操作）
            AdminDTO admin = AdminHolder.getAdmin();
            if (admin != null) {
                operationLog.setAdminId(admin.getId());
            }

            // 设置操作信息
            String operationDesc = logAnnotation.module() + " - " + logAnnotation.operation();
            operationLog.setOperation(operationDesc);
            operationLog.setMethod(joinPoint.getSignature().getName());

            // 设置请求参数（限制长度）
            try {
                String params = JSON.toJSONString(joinPoint.getArgs());
                operationLog.setParams(params.length() > 1000 ? params.substring(0, 1000) + "..." : params);
            } catch (Exception e) {
                operationLog.setParams("[参数序列化失败]");
            }

            // 设置IP
            operationLog.setIp(getClientIp(request));
            operationLog.setCreateTime(LocalDateTime.now());

            // 异步保存到数据库
            operationLogService.saveLog(operationLog);

        } catch (Exception e) {
            log.error("保存操作日志到数据库失败", e);
        }
    }

    /**
     * 保存到审计日志
     */
    private void saveToAuditLog(ProceedingJoinPoint joinPoint, Object result, long timeTaken,
                               Exception exception, HttpServletRequest request, Log logAnnotation) {
        try {
            AuditLog auditLog = new AuditLog();

            // 设置日志类型为操作日志
            auditLog.setLogType(1);

            // 设置操作人信息
            AdminDTO admin = AdminHolder.getAdmin();
            UserDTO user = UserHolder.getUser();

            if (admin != null) {
                auditLog.setOperatorId(admin.getId());
                auditLog.setOperatorType(1); // 管理员
                auditLog.setOperatorName(admin.getUsername());
            } else if (user != null) {
                auditLog.setOperatorId(user.getId());
                auditLog.setOperatorType(2); // 用户
                auditLog.setOperatorName(user.getNickName());
            }

            // 设置操作信息
            auditLog.setModule(logAnnotation.module());
            auditLog.setOperation(logAnnotation.operation());
            auditLog.setDescription(logAnnotation.module() + " - " + logAnnotation.operation());
            auditLog.setMethod(request.getMethod());
            auditLog.setUrl(request.getRequestURI());

            // 设置请求参数
            try {
                String params = JSON.toJSONString(joinPoint.getArgs());
                auditLog.setParams(params.length() > 2000 ? params.substring(0, 2000) + "..." : params);
            } catch (Exception e) {
                auditLog.setParams("[参数序列化失败]");
            }

            // 设置返回结果
            try {
                String resultStr = JSON.toJSONString(result);
                auditLog.setResult(resultStr.length() > 2000 ? resultStr.substring(0, 2000) + "..." : resultStr);
            } catch (Exception e) {
                auditLog.setResult("[结果序列化失败]");
            }

            // 设置状态和执行时间
            auditLog.setStatus(exception == null ? 1 : 0);
            auditLog.setErrorMsg(exception != null ? exception.getMessage() : null);
            auditLog.setExecuteTime(timeTaken);

            // 设置IP和用户代理
            auditLog.setIp(getClientIp(request));
            auditLog.setUserAgent(request.getHeader("User-Agent"));

            // 设置风险等级（简单判断）
            if (exception != null) {
                auditLog.setRiskLevel(2); // 中风险
            } else if (timeTaken > 5000) {
                auditLog.setRiskLevel(2); // 响应时间过长
            } else {
                auditLog.setRiskLevel(1); // 低风险
            }

            // 异步保存到审计日志
            auditLogService.saveOperationLog(auditLog);

        } catch (Exception e) {
            log.error("保存审计日志失败", e);
        }
    }
}
