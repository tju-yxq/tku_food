package xyz.tjucomments.tjufood.utils.constants;

import org.springframework.stereotype.Component;

/**
 * @author Gemini
 * @description 系统级常量定义
 * @create 2025-06-16 14:15
 */
@Component
public class SystemConstants {

    /**
     * 图片上传目录，此字段由Spring通过@Value注解自动注入，因此不能是static final的。
     * 为了在静态上下文中使用，可以在需要的地方通过@Resource注入SystemConstants实例来访问。
     */
    //  @Value("${tjufood.image-upload-dir}") // 原始配置，保持不变
    //  public String imageUploadDir;

    /**
     * 用户默认昵称前缀
     */
    public static final String USER_NICK_NAME_PREFIX = "user_";

    /**
     * 默认分页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 5;

    /**
     * 最大分页大小
     */
    public static final int MAX_PAGE_SIZE = 10;

    /**
     * 【新增】管理员默认初始密码
     */
    public static final String ADMIN_DEFAULT_PASSWORD = "123456";

}
