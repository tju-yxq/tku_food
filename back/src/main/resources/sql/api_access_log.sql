-- API访问日志表
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

-- 创建索引
CREATE INDEX idx_api_access_path ON tb_api_access_log(api_path);
CREATE INDEX idx_api_access_method ON tb_api_access_log(http_method);
CREATE INDEX idx_api_access_status ON tb_api_access_log(response_status);
CREATE INDEX idx_api_access_time ON tb_api_access_log(create_time);
CREATE INDEX idx_api_access_caller ON tb_api_access_log(caller_id);
CREATE INDEX idx_api_access_response_time ON tb_api_access_log(response_time);

-- 插入一些测试数据
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
(10, '/api/user/register', 'POST', '用户注册', NULL, 3, '匿名用户', 400, 45, 0, '192.168.1.104', 'trace-010', DATEADD(MINUTE, -15, GETDATE()));
