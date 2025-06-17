-- 审计日志表
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

-- 创建索引
CREATE INDEX idx_audit_log_type ON tb_audit_log(log_type);
CREATE INDEX idx_audit_log_operator ON tb_audit_log(operator_id);
CREATE INDEX idx_audit_log_time ON tb_audit_log(create_time);
CREATE INDEX idx_audit_log_module ON tb_audit_log(module);

-- 插入一些测试数据
INSERT INTO tb_audit_log (id, log_type, operator_id, operator_type, operator_name, module, operation, description, method, url, status, execute_time, ip, create_time) VALUES
(1, 1, 1, 1, 'sysadmin', '用户管理', '查询用户列表', '管理员查询用户列表', 'GET', '/api/admin/user/list', 1, 125, '192.168.1.100', DATEADD(HOUR, -2, GETDATE())),
(2, 1, 1, 1, 'sysadmin', '内容管理', '添加食堂', '管理员添加新食堂', 'POST', '/api/admin/canteen/add', 1, 89, '192.168.1.100', DATEADD(HOUR, -1, GETDATE())),
(3, 2, 1, 1, 'sysadmin', '系统管理', '登录成功', '管理员登录系统成功', 'POST', '/api/admin/login', 1, 156, '192.168.1.100', DATEADD(MINUTE, -30, GETDATE())),
(4, 1, 1, 1, 'sysadmin', '运营管理', '更新轮播图', '管理员更新首页轮播图', 'PUT', '/api/admin/banner/update', 1, 234, '192.168.1.100', DATEADD(MINUTE, -15, GETDATE())),
(5, 3, NULL, NULL, NULL, '安全管理', '异常登录尝试', '检测到异常IP登录尝试', 'POST', '/api/admin/login', 0, 0, '192.168.1.200', DATEADD(MINUTE, -10, GETDATE()));
