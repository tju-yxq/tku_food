-- ========================================
-- 完整修复管理员表结构和角色管理问题
-- ========================================

-- 1. 首先修复管理员状态问题（紧急）
PRINT '=== 紧急修复管理员状态 ===';
UPDATE tb_admin SET 
    status = 0,  -- 0=启用
    update_time = GETDATE()
WHERE status != 0;

-- 2. 为管理员表添加电话号码和邮箱字段
PRINT '=== 扩展管理员表结构 ===';

-- 检查并添加电话号码字段
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'tb_admin' AND COLUMN_NAME = 'phone')
BEGIN
    ALTER TABLE tb_admin ADD phone NVARCHAR(20) NULL;
    PRINT '已添加电话号码字段';
END

-- 检查并添加邮箱字段
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'tb_admin' AND COLUMN_NAME = 'email')
BEGIN
    ALTER TABLE tb_admin ADD email NVARCHAR(100) NULL;
    PRINT '已添加邮箱字段';
END

-- 3. 确保角色数据存在
PRINT '=== 确保角色数据存在 ===';
IF NOT EXISTS (SELECT 1 FROM tb_role)
BEGIN
    PRINT '插入基础角色数据...';
    INSERT INTO tb_role (name, description, status, create_time, update_time) VALUES
    ('超级管理员', '拥有系统所有权限，负责系统整体管理和维护', 0, GETDATE(), GETDATE()),
    ('运营管理员', '负责日常运营管理，包括内容审核、用户管理等', 0, GETDATE(), GETDATE()),
    ('内容管理员', '负责管理食堂、窗口、菜品等核心基础数据', 0, GETDATE(), GETDATE()),
    ('客服管理员', '负责处理用户反馈、投诉建议等客服相关工作', 0, GETDATE(), GETDATE());
END
ELSE
BEGIN
    PRINT '角色数据已存在，跳过插入';
END

-- 4. 为管理员分配角色和联系信息
PRINT '=== 更新管理员信息 ===';

UPDATE tb_admin SET 
    role_id = 1,
    status = 0,
    phone = '13800138001',
    email = 'sysadmin@tjufood.com',
    update_time = GETDATE()
WHERE username = 'sysadmin';

UPDATE tb_admin SET 
    role_id = 2,
    status = 0,
    phone = '13800138002',
    email = 'operation@tjufood.com',
    update_time = GETDATE()
WHERE username = 'operationadmin';

UPDATE tb_admin SET 
    role_id = 3,
    status = 0,
    phone = '13800138003',
    email = 'content@tjufood.com',
    update_time = GETDATE()
WHERE username = 'contentadmin';

UPDATE tb_admin SET 
    role_id = 4,
    status = 0,
    phone = '13800138004',
    email = 'service@tjufood.com',
    update_time = GETDATE()
WHERE username = 'serviceadmin';

-- 5. 确保窗口类型数据存在
PRINT '=== 确保窗口类型数据存在 ===';
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'tb_stall_type')
BEGIN
    CREATE TABLE tb_stall_type (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        name NVARCHAR(50) NOT NULL,
        description NVARCHAR(255) NULL,
        icon NVARCHAR(255) NULL,
        sort INT NULL,
        create_time DATETIME2 NOT NULL DEFAULT GETDATE(),
        update_time DATETIME2 NOT NULL DEFAULT GETDATE()
    );
    PRINT '已创建窗口类型表';
END

-- 清理并重新插入窗口类型数据
DELETE FROM tb_stall_type;
DBCC CHECKIDENT ('tb_stall_type', RESEED, 0);

INSERT INTO tb_stall_type (name, description, create_time, update_time) VALUES
('主食窗口', '提供米饭、面条等主食类食品', GETDATE(), GETDATE()),
('面食窗口', '提供各类面条、饺子、包子等面食', GETDATE(), GETDATE()),
('快餐窗口', '提供快餐、盖浇饭等快速餐食', GETDATE(), GETDATE()),
('特色窗口', '提供地方特色菜、民族菜等特色餐食', GETDATE(), GETDATE()),
('小食窗口', '提供小食、零食、饮品等轻食', GETDATE(), GETDATE()),
('清真窗口', '提供符合清真标准的餐食', GETDATE(), GETDATE()),
('素食窗口', '提供素食、健康餐等特殊饮食需求', GETDATE(), GETDATE()),
('自选窗口', '提供自选菜品、自助餐等形式', GETDATE(), GETDATE());

-- 6. 验证修复结果
PRINT '=== 验证修复结果 ===';

-- 查看管理员表结构
PRINT '管理员表字段：';
SELECT 
    COLUMN_NAME as 字段名,
    DATA_TYPE as 数据类型,
    CHARACTER_MAXIMUM_LENGTH as 最大长度,
    IS_NULLABLE as 允许NULL
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'tb_admin'
ORDER BY ORDINAL_POSITION;

-- 查看管理员数据
PRINT '管理员数据：';
SELECT 
    a.id,
    a.username,
    a.name,
    a.phone,
    a.email,
    r.name as role_name,
    CASE 
        WHEN a.status = 0 THEN '启用'
        WHEN a.status = 1 THEN '禁用'
        ELSE '未知状态'
    END as status_desc,
    a.status as status_code,
    a.role_id
FROM tb_admin a
LEFT JOIN tb_role r ON a.role_id = r.id
ORDER BY a.id;

-- 查看角色数据
PRINT '角色数据：';
SELECT 
    id,
    name,
    description,
    CASE 
        WHEN status = 0 THEN '启用'
        WHEN status = 1 THEN '禁用'
        ELSE '未知状态'
    END as status_desc,
    status as status_code
FROM tb_role
ORDER BY id;

-- 查看窗口类型数据
PRINT '窗口类型数据：';
SELECT 
    id,
    name,
    description
FROM tb_stall_type
ORDER BY id;

-- 7. 显示修复统计
DECLARE @adminCount INT, @roleCount INT, @stallTypeCount INT;
SELECT @adminCount = COUNT(*) FROM tb_admin WHERE status = 0;
SELECT @roleCount = COUNT(*) FROM tb_role WHERE status = 0;
SELECT @stallTypeCount = COUNT(*) FROM tb_stall_type;

PRINT '=== 修复完成统计 ===';
PRINT '启用状态管理员数量: ' + CAST(@adminCount AS NVARCHAR(10));
PRINT '启用状态角色数量: ' + CAST(@roleCount AS NVARCHAR(10));
PRINT '窗口类型数量: ' + CAST(@stallTypeCount AS NVARCHAR(10));
PRINT '修复完成！所有管理员现在都可以正常登录，并且已添加联系方式字段。';
