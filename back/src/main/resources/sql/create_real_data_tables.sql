-- ========================================
-- 企业级审计日志和API管理真实数据表
-- ========================================

-- 1. 审计日志表
CREATE TABLE tb_audit_log (
    id BIGINT PRIMARY KEY,
    log_type INT NOT NULL COMMENT '日志类型 (1=操作日志, 2=登录日志, 3=安全日志)',
    operator_id BIGINT COMMENT '操作人ID',
    operator_type INT COMMENT '操作人类型 (1=管理员, 2=用户)',
    operator_name NVARCHAR(100) COMMENT '操作人名称',
    module NVARCHAR(50) COMMENT '业务模块',
    operation NVARCHAR(100) COMMENT '操作类型',
    description NVARCHAR(500) COMMENT '操作描述',
    method NVARCHAR(10) COMMENT '请求方法',
    url NVARCHAR(500) COMMENT '请求URL',
    params NTEXT COMMENT '请求参数',
    result NTEXT COMMENT '返回结果',
    status INT DEFAULT 1 COMMENT '操作状态 (0=失败, 1=成功)',
    error_msg NVARCHAR(500) COMMENT '错误信息',
    execute_time BIGINT COMMENT '执行时间(毫秒)',
    ip NVARCHAR(50) COMMENT '操作IP',
    ip_location NVARCHAR(200) COMMENT 'IP归属地',
    user_agent NVARCHAR(500) COMMENT '用户代理',
    browser NVARCHAR(50) COMMENT '浏览器类型',
    os NVARCHAR(50) COMMENT '操作系统',
    device_type NVARCHAR(50) COMMENT '设备类型',
    risk_level INT DEFAULT 1 COMMENT '风险等级 (1=低, 2=中, 3=高)',
    create_time DATETIME2 DEFAULT GETDATE() COMMENT '创建时间'
);

-- 2. API访问日志表
CREATE TABLE tb_api_access_log (
    id BIGINT PRIMARY KEY,
    api_path NVARCHAR(500) NOT NULL COMMENT 'API路径',
    http_method NVARCHAR(10) NOT NULL COMMENT 'HTTP方法',
    api_name NVARCHAR(200) COMMENT 'API名称/描述',
    api_version NVARCHAR(20) DEFAULT 'v1.0' COMMENT 'API版本',
    caller_id BIGINT COMMENT '调用者ID',
    caller_type INT COMMENT '调用者类型 (1=管理员, 2=用户, 3=匿名)',
    caller_name NVARCHAR(100) COMMENT '调用者名称',
    request_params NTEXT COMMENT '请求参数',
    request_headers NTEXT COMMENT '请求头信息',
    request_size BIGINT COMMENT '请求体大小(字节)',
    response_status INT NOT NULL COMMENT '响应状态码',
    response_data NTEXT COMMENT '响应数据',
    response_size BIGINT COMMENT '响应体大小(字节)',
    response_time BIGINT NOT NULL COMMENT '响应时间(毫秒)',
    success INT DEFAULT 1 COMMENT '是否成功 (0=失败, 1=成功)',
    error_code NVARCHAR(50) COMMENT '错误代码',
    error_message NVARCHAR(500) COMMENT '错误信息',
    client_ip NVARCHAR(50) COMMENT '客户端IP',
    ip_location NVARCHAR(200) COMMENT 'IP归属地',
    user_agent NVARCHAR(500) COMMENT '用户代理',
    referer NVARCHAR(500) COMMENT '请求来源',
    trace_id NVARCHAR(100) COMMENT '请求ID/追踪ID',
    create_time DATETIME2 DEFAULT GETDATE() COMMENT '创建时间'
);

-- 3. API版本管理表
CREATE TABLE tb_api_version (
    id BIGINT PRIMARY KEY,
    version_number NVARCHAR(20) NOT NULL COMMENT '版本号',
    version_name NVARCHAR(100) COMMENT '版本名称',
    release_date DATETIME2 COMMENT '发布日期',
    status NVARCHAR(20) DEFAULT '开发中' COMMENT '状态 (开发中, 测试中, 已发布, 已废弃)',
    description NVARCHAR(1000) COMMENT '版本说明',
    changelog NTEXT COMMENT '更新日志',
    usage_count BIGINT DEFAULT 0 COMMENT '使用次数',
    is_current INT DEFAULT 0 COMMENT '是否当前版本 (0=否, 1=是)',
    create_time DATETIME2 DEFAULT GETDATE() COMMENT '创建时间',
    update_time DATETIME2 DEFAULT GETDATE() COMMENT '更新时间'
);

-- 4. 系统性能监控表
CREATE TABLE tb_system_performance (
    id BIGINT PRIMARY KEY,
    metric_name NVARCHAR(100) NOT NULL COMMENT '指标名称',
    metric_value DECIMAL(18,4) COMMENT '指标值',
    metric_unit NVARCHAR(20) COMMENT '指标单位',
    metric_type NVARCHAR(50) COMMENT '指标类型 (CPU, Memory, Disk, Network, API)',
    record_time DATETIME2 DEFAULT GETDATE() COMMENT '记录时间',
    create_time DATETIME2 DEFAULT GETDATE() COMMENT '创建时间'
);

-- 创建索引
CREATE INDEX idx_audit_log_type ON tb_audit_log(log_type);
CREATE INDEX idx_audit_log_operator ON tb_audit_log(operator_id);
CREATE INDEX idx_audit_log_time ON tb_audit_log(create_time);
CREATE INDEX idx_audit_log_module ON tb_audit_log(module);

CREATE INDEX idx_api_access_path ON tb_api_access_log(api_path);
CREATE INDEX idx_api_access_method ON tb_api_access_log(http_method);
CREATE INDEX idx_api_access_status ON tb_api_access_log(response_status);
CREATE INDEX idx_api_access_time ON tb_api_access_log(create_time);
CREATE INDEX idx_api_access_caller ON tb_api_access_log(caller_id);
CREATE INDEX idx_api_access_response_time ON tb_api_access_log(response_time);

CREATE INDEX idx_api_version_status ON tb_api_version(status);
CREATE INDEX idx_api_version_current ON tb_api_version(is_current);

CREATE INDEX idx_system_performance_type ON tb_system_performance(metric_type);
CREATE INDEX idx_system_performance_time ON tb_system_performance(record_time);

-- 插入API版本测试数据
INSERT INTO tb_api_version (id, version_number, version_name, release_date, status, description, usage_count, is_current) VALUES
(1, 'v1.2.0', 'TjuFood API v1.2.0', DATEADD(DAY, -30, GETDATE()), '已发布', '新增API文档管理功能，优化性能监控，增强安全防护', 15420, 1),
(2, 'v1.1.0', 'TjuFood API v1.1.0', DATEADD(DAY, -90, GETDATE()), '已发布', '增加操作日志功能，修复已知问题，优化数据库性能', 8950, 0),
(3, 'v1.0.0', 'TjuFood API v1.0.0', DATEADD(DAY, -180, GETDATE()), '已废弃', '初始版本，基础功能实现', 2340, 0),
(4, 'v1.3.0', 'TjuFood API v1.3.0', DATEADD(DAY, 15, GETDATE()), '开发中', '计划增加AI推荐功能，优化用户体验', 0, 0);

-- 插入系统性能监控测试数据
INSERT INTO tb_system_performance (id, metric_name, metric_value, metric_unit, metric_type, record_time) VALUES
(1, 'CPU使用率', 45.6, '%', 'CPU', DATEADD(MINUTE, -5, GETDATE())),
(2, '内存使用率', 68.2, '%', 'Memory', DATEADD(MINUTE, -5, GETDATE())),
(3, '磁盘使用率', 72.8, '%', 'Disk', DATEADD(MINUTE, -5, GETDATE())),
(4, '网络吞吐量', 125.4, 'MB/s', 'Network', DATEADD(MINUTE, -5, GETDATE())),
(5, 'API平均响应时间', 125.8, 'ms', 'API', DATEADD(MINUTE, -5, GETDATE())),
(6, 'API成功率', 99.2, '%', 'API', DATEADD(MINUTE, -5, GETDATE())),
(7, '并发用户数', 1250, '个', 'API', DATEADD(MINUTE, -5, GETDATE())),
(8, '数据库连接数', 45, '个', 'Database', DATEADD(MINUTE, -5, GETDATE()));

-- 插入审计日志测试数据
INSERT INTO tb_audit_log (id, log_type, operator_id, operator_type, operator_name, module, operation, description, method, url, status, execute_time, ip, create_time) VALUES
(1, 1, 1, 1, 'sysadmin', '用户管理', '查询用户列表', '管理员查询用户列表', 'GET', '/api/admin/user/list', 1, 125, '192.168.1.100', DATEADD(HOUR, -2, GETDATE())),
(2, 1, 1, 1, 'sysadmin', '内容管理', '添加食堂', '管理员添加新食堂', 'POST', '/api/admin/canteen/add', 1, 89, '192.168.1.100', DATEADD(HOUR, -1, GETDATE())),
(3, 2, 1, 1, 'sysadmin', '系统管理', '登录成功', '管理员登录系统成功', 'POST', '/api/admin/login', 1, 156, '192.168.1.100', DATEADD(MINUTE, -30, GETDATE())),
(4, 1, 1, 1, 'sysadmin', '运营管理', '更新轮播图', '管理员更新首页轮播图', 'PUT', '/api/admin/banner/update', 1, 234, '192.168.1.100', DATEADD(MINUTE, -15, GETDATE())),
(5, 3, NULL, NULL, NULL, '安全管理', '异常登录尝试', '检测到异常IP登录尝试', 'POST', '/api/admin/login', 0, 0, '192.168.1.200', DATEADD(MINUTE, -10, GETDATE())),
(6, 1, 2, 2, 'user123', '社区管理', '发布博客', '用户发布新博客', 'POST', '/api/blog/create', 1, 345, '192.168.1.101', DATEADD(MINUTE, -25, GETDATE())),
(7, 1, 3, 2, 'user456', '社区管理', '添加评论', '用户添加评论', 'POST', '/api/comment/add', 1, 78, '192.168.1.102', DATEADD(MINUTE, -20, GETDATE())),
(8, 2, 2, 2, 'user123', '用户管理', '登录成功', '用户登录成功', 'POST', '/api/user/login', 1, 95, '192.168.1.101', DATEADD(MINUTE, -35, GETDATE())),
(9, 1, 1, 1, 'sysadmin', '系统管理', '删除用户', '管理员删除违规用户', 'DELETE', '/api/admin/user/delete', 1, 156, '192.168.1.100', DATEADD(MINUTE, -40, GETDATE())),
(10, 3, NULL, NULL, NULL, '安全管理', '频繁请求', '检测到IP频繁请求', 'GET', '/api/canteen/list', 0, 0, '192.168.1.201', DATEADD(MINUTE, -5, GETDATE()));

-- 插入API访问日志测试数据
INSERT INTO tb_api_access_log (id, api_path, http_method, api_name, caller_id, caller_type, caller_name, response_status, response_time, success, client_ip, trace_id, create_time) VALUES
(1, '/api/user/login', 'POST', '用户登录', 2, 2, 'user123', 200, 95, 1, '192.168.1.101', 'trace-001', DATEADD(MINUTE, -60, GETDATE())),
(2, '/api/canteen/list', 'GET', '获取食堂列表', 2, 2, 'user123', 200, 120, 1, '192.168.1.101', 'trace-002', DATEADD(MINUTE, -55, GETDATE())),
(3, '/api/dish/search', 'GET', '搜索菜品', 3, 2, 'user456', 200, 180, 1, '192.168.1.102', 'trace-003', DATEADD(MINUTE, -50, GETDATE())),
(4, '/api/user/profile', 'GET', '获取用户信息', 2, 2, 'user123', 200, 85, 1, '192.168.1.101', 'trace-004', DATEADD(MINUTE, -45, GETDATE())),
(5, '/api/blog/create', 'POST', '创建博客', 3, 2, 'user456', 201, 250, 1, '192.168.1.102', 'trace-005', DATEADD(MINUTE, -40, GETDATE())),
(6, '/api/admin/user/list', 'GET', '管理员查询用户', 1, 1, 'sysadmin', 200, 156, 1, '192.168.1.100', 'trace-006', DATEADD(MINUTE, -35, GETDATE())),
(7, '/api/comment/add', 'POST', '添加评论', 4, 2, 'user789', 200, 89, 1, '192.168.1.103', 'trace-007', DATEADD(MINUTE, -30, GETDATE())),
(8, '/api/upload/image', 'POST', '上传图片', 2, 2, 'user123', 413, 1200, 0, '192.168.1.101', 'trace-008', DATEADD(MINUTE, -25, GETDATE())),
(9, '/api/statistics/dashboard', 'GET', '获取统计数据', 1, 1, 'sysadmin', 200, 2500, 1, '192.168.1.100', 'trace-009', DATEADD(MINUTE, -20, GETDATE())),
(10, '/api/user/register', 'POST', '用户注册', NULL, 3, '匿名用户', 400, 45, 0, '192.168.1.104', 'trace-010', DATEADD(MINUTE, -15, GETDATE())),
(11, '/api/canteen/detail', 'GET', '获取食堂详情', 2, 2, 'user123', 200, 145, 1, '192.168.1.101', 'trace-011', DATEADD(MINUTE, -12, GETDATE())),
(12, '/api/dish/list', 'GET', '获取菜品列表', 3, 2, 'user456', 200, 98, 1, '192.168.1.102', 'trace-012', DATEADD(MINUTE, -10, GETDATE())),
(13, '/api/blog/list', 'GET', '获取博客列表', 4, 2, 'user789', 200, 234, 1, '192.168.1.103', 'trace-013', DATEADD(MINUTE, -8, GETDATE())),
(14, '/api/user/logout', 'POST', '用户登出', 2, 2, 'user123', 200, 45, 1, '192.168.1.101', 'trace-014', DATEADD(MINUTE, -5, GETDATE())),
(15, '/api/admin/statistics', 'GET', '管理员统计', 1, 1, 'sysadmin', 200, 1890, 1, '192.168.1.100', 'trace-015', DATEADD(MINUTE, -3, GETDATE()));
