# TjuFood - 通用工具与核心组件使用指南

[TOC]

## 1. 引言

### 1.1 文档目的

本文档旨在为TjuFood项目的开发团队提供一份清晰、详尽的公共组件使用规范。它详细阐述了`xyz.tjucomments.tjufood.utils`包及其相关核心组件的设计思想、功能、具体使用方法及注意事项。遵循本指南将有助于提升开发效率、保证代码质量，并确保系统架构的一致性和健壮性。

### 1.2 前置条件

在开始使用这些工具前，请确保您的本地开发环境和项目配置已满足以下条件：

1.  **Maven依赖已添加**: 确保 `pom.xml` 文件中已包含 `hutool-all`、`spring-boot-starter-data-redis`、`spring-boot-starter-validation` 等必要依赖 [cite: back/pom.xml]。
2.  **线程池配置已创建**: 项目中存在 `ThreadPoolConfig.java`，它为缓存重建等异步任务提供了专用的线程池支持 [cite: back/src/main/java/xyz/tjucomments/tjufood/config/ThreadPoolConfig.java]。
3.  **Lua脚本已放置**: 在 `src/main/resources/lua/` 目录下已包含 `unlock.lua` 脚本，用于分布式锁的原子性释放 [cite: back/src/main/resources/lua/unlock.lua]。
4.  **配置文件已更新**: `application.yaml` 中已正确配置数据库、Redis的连接信息以及项目自定义的路径等 [cite: back/src/main/resources/application.yaml]。

---

## 2. 核心工具与组件详解

### 2.1 常量包 (`utils.constants.*`)

-   **核心类**:
    -   `RedisConstants`: 管理所有与Redis交互的Key前缀和TTL（有效期）。
    -   `SystemConstants`: 管理系统级的业务常量，如默认密码、分页大小等。
    -   `IdPrefixConstants`: 管理`RedisIdWorker`生成ID时使用的业务前缀。
-   **设计思想**: **消除魔术值 (Magic Values)**。通过集中管理常量，提高代码的可读性和可维护性。当需要修改某个Key或TTL时，只需改动一处，即可在整个项目中生效。
-   **使用规范**:
    -   **严禁硬编码**: 在代码中严禁出现如`"login:code:"`这样的字符串字面量。
    -   **正确引用**: 应始终通过`类名.常量名`的方式引用，例如：
        ```java
        // 正确用法
        String key = RedisConstants.LOGIN_CODE_KEY + email;
        long userId = redisIdWorker.nextId(IdPrefixConstants.USER_ID_PREFIX);
        ```

### 2.2 缓存工具 (`utils.cache.*`)

-   **核心类**: `CacheClient`
-   **辅助类**: `RedisData`
-   **设计思想**: 封装复杂的Redis缓存模式，为业务层提供简单、统一的调用接口。它内置了**缓存穿透**和**缓存击穿**两种业界成熟的解决方案。
-   **如何使用**:
    在需要缓存的Service实现类中注入`CacheClient`。

    -   **场景A：解决“缓存穿透”**
        使用 `queryWithPassThrough` 方法。适用于**数据量不大、更新不频繁但可能被无效ID频繁攻击**的场景（如查询单个食堂、窗口信息）。
        ```java
        // 在 CanteenServiceImpl.java 中
        public Result queryCanteenById(Long id) {
            CanteenDTO canteen = cacheClient.queryWithPassThrough(
                    RedisConstants.CACHE_CANTEEN_KEY, // 缓存Key的前缀
                    id,                            // 业务ID
                    CanteenDTO.class,              // 期望返回的对象类型
                    canteenId -> baseMapper.queryCanteenById(canteenId), // 缓存未命中时，查询DB的逻辑
                    RedisConstants.CACHE_CANTEEN_TTL, // 缓存有效期
                    TimeUnit.MINUTES               // 时间单位
            );
            if (canteen == null) {
                return Result.fail("食堂不存在！");
            }
            return Result.ok(canteen);
        }
        ```

    -   **场景B：解决“缓存击穿”**
        使用 `queryWithLogicalExpire` 方法。适用于**访问极其频繁的热点数据**，即使短暂返回旧数据也比让大量请求直接冲击数据库要好。
        ```java
        // 示例：假设未来有一个热点活动需要缓存
        public ActivityDTO queryHotActivity(Long id) {
            return cacheClient.queryWithLogicalExpire(
                    RedisConstants.CACHE_ACTIVITY_KEY, // 缓存Key前缀
                    RedisConstants.LOCK_ACTIVITY_KEY,  // 对应的分布式锁前缀
                    id,
                    ActivityDTO.class,
                    activityId -> activityMapper.selectById(activityId),
                    RedisConstants.CACHE_ACTIVITY_TTL,
                    TimeUnit.SECONDS
            );
        }
        ```

### 2.3 分布式ID生成器 (`utils.id.*`)

-   **核心类**: `RedisIdWorker`
-   **设计思想**: 利用Redis的原子自增命令`INCR`，结合时间戳和序列号，生成一个64位的、全局唯一的、趋势递增的ID。这为未来系统的水平扩展奠定了基础。
-   **使用规范**:
    -   在需要生成ID的Service中注入`RedisIdWorker`。
    -   调用`nextId()`方法时，必须传入一个在`IdPrefixConstants`中预定义的**业务前缀**。
        ```java
        // 在 UserServiceImpl.java 中为新用户生成ID
        long userId = redisIdWorker.nextId(IdPrefixConstants.USER_ID_PREFIX); 
        user.setId(userId);
        ```

### 2.4 分布式锁 (`utils.lock.*`)

-   **核心类**: `SimpleRedisLock`
-   **接口**: `ILock`
-   **设计思想**: 基于Redis的`SETNX`命令实现一个简单的、非可重入的分布式锁，通过Lua脚本保证锁释放的原子性。
-   **使用规范**:
    -   **必须使用 `try-finally` 结构**，确保无论业务逻辑是否抛出异常，锁最终都能被释放，避免产生死锁。
        ```java
        @Resource
        private StringRedisTemplate stringRedisTemplate;
        
        public Result someConcurrentMethod(Long businessId) {
            // 1. 根据业务ID创建锁实例，确保锁的粒度精确
            ILock lock = new SimpleRedisLock("unique_business_key:" + businessId, stringRedisTemplate);
            
            // 2. 尝试获取锁，并设置合理的超时时间
            if (!lock.tryLock(10)) { // 尝试获取锁，最多等待10秒
                return Result.fail("操作频繁，请稍后再试");
            }
            
            try {
                // 3. --- 执行核心业务逻辑 ---
                // ...
                return Result.ok();
            } finally {
                // 4. 在finally块中释放锁
                lock.unlock(); 
            }
        }
        ```

### 2.5 线程上下文持有器 (`interceptor.*Holder`)

-   **核心类**: `UserHolder`, `AdminHolder`
-   **设计思想**: 利用`ThreadLocal`机制，在**同一个请求的处理线程**中创建一个隔离的存储空间，用于传递当前登录的用户信息（`UserDTO`或`AdminDTO`）。这极大地简化了代码，避免了在各层方法之间繁琐地传递`userId`或`adminId`参数。
-   **工作流程**:
    1.  **存入**: `RefreshTokenInterceptor` 和 `AdminRefreshTokenInterceptor` 在验证Token成功后，会自动将用户信息存入相应的Holder [cite: back/src/main/java/xyz/tjucomments/tjufood/interceptor/AdminRefreshTokenInterceptor.java, back/src/main/java/xyz/tjucomments/tjufood/interceptor/RefreshTokenInterceptor.java]。**开发者通常无需关心存入操作**。
    2.  **获取**: 在Service层或Controller层的任何地方，通过静态方法直接获取。
        ```java
        // 在某个需要用户ID的Service方法中
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            // 处理未登录或异常情况
            return Result.fail("用户未登录");
        }
        Long currentUserId = user.getId();
        // ... 使用currentUserId进行业务操作 ...
        ```
    3.  **移除**: `Interceptor`的`afterCompletion`方法会在请求处理结束后自动调用`removeUser()`或`removeAdmin()`来清理`ThreadLocal`，**防止内存泄漏**。**开发者同样无需关心此操作**。

### 2.6 全局异常处理器 (`advice.GlobalExceptionHandler`)

-   **核心类**: `GlobalExceptionHandler`
-   **设计思想**: 采用AOP思想，通过`@RestControllerAdvice`注解将该类声明为一个全局的Controller增强器。它能统一捕获所有Controller层抛出的异常，是保证API高可用性和安全性的重要防线。
-   **它是如何工作的**:
    -   **参数校验异常**: 当使用了`@Validated`注解的DTO参数校验失败时，`handleMethodArgumentNotValidException`方法会被触发，返回具体的字段错误信息。
    -   **通用异常**: 当代码中出现任何其他未被捕获的`Exception`时，`handleException`方法会被触发，它会**在服务端记录详细的错误日志**（用于排查问题），同时**向前端返回一个通用的、友好的错误提示**（“服务器开小差了...”），避免暴露系统内部实现细节。
-   **使用规范**: **开发者无需直接与它交互**。只需确保在需要校验的Controller方法参数前添加`@Validated`注解即可。