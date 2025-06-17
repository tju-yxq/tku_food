package xyz.tjucomments.tjufood.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Gemini
 * @description 自定义操作日志记录注解
 * @create 2025-06-16 14:15
 */
@Target(ElementType.METHOD) // 注解作用于方法
@Retention(RetentionPolicy.RUNTIME) // 运行时保留，以便AOP可以读取
public @interface Log {

    /**
     * 操作模块名称
     */
    String module() default "";

    /**
     * 具体操作描述
     */
    String operation();
}
