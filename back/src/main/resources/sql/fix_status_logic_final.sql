-- ========================================
-- 修复状态字段逻辑（最终正确版本）
-- 统一状态逻辑：0=启用/正常，1=禁用
-- ========================================

-- 1. 清理并重新插入角色数据
-- 先删除现有角色数据（如果存在）
DELETE FROM tb_admin WHERE role_id IN (1, 2, 3, 4);
DELETE FROM tb_role WHERE id IN (1, 2, 3, 4);

-- 重置自增ID
DBCC CHECKIDENT ('tb_role', RESEED, 0);

-- 插入角色数据（让数据库自动分配ID）
-- 状态：0=启用，1=禁用
INSERT INTO tb_role (name, description, status, create_time, update_time) VALUES
('超级管理员', '拥有系统所有权限，负责系统整体管理和维护', 0, GETDATE(), GETDATE()),
('运营管理员', '负责日常运营管理，包括内容审核、用户管理等', 0, GETDATE(), GETDATE()),
('内容管理员', '负责管理食堂、窗口、菜品等核心基础数据', 0, GETDATE(), GETDATE()),
('客服管理员', '负责处理用户反馈、投诉建议等客服相关工作', 0, GETDATE(), GETDATE());

-- 2. 更新管理员的角色分配和状态
-- 状态：0=启用，1=禁用
UPDATE tb_admin SET 
    role_id = 1,
    status = 0,  -- 0=启用
    update_time = GETDATE()
WHERE username = 'sysadmin';

UPDATE tb_admin SET 
    role_id = 2,
    status = 0,  -- 0=启用
    update_time = GETDATE()
WHERE username = 'operationadmin';

UPDATE tb_admin SET 
    role_id = 3,
    status = 0,  -- 0=启用
    update_time = GETDATE()
WHERE username = 'contentadmin';

UPDATE tb_admin SET 
    role_id = 4,
    status = 0,  -- 0=启用
    update_time = GETDATE()
WHERE username = 'serviceadmin';

-- 3. 插入窗口类型数据（如果表不存在则创建）
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

-- 4. 插入食堂类型数据（如果表不存在则创建）
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'tb_canteen_type')
BEGIN
    CREATE TABLE tb_canteen_type (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        name NVARCHAR(50) NOT NULL,
        description NVARCHAR(255) NULL,
        icon NVARCHAR(255) NULL,
        sort INT NULL,
        create_time DATETIME2 NOT NULL DEFAULT GETDATE(),
        update_time DATETIME2 NOT NULL DEFAULT GETDATE()
    );
END

-- 清理并重新插入食堂类型数据
DELETE FROM tb_canteen_type;
DBCC CHECKIDENT ('tb_canteen_type', RESEED, 0);

INSERT INTO tb_canteen_type (name, description, create_time, update_time) VALUES
('学生食堂', '主要为学生提供餐饮服务的食堂', GETDATE(), GETDATE()),
('教工食堂', '主要为教职工提供餐饮服务的食堂', GETDATE(), GETDATE()),
('清真食堂', '提供清真餐饮的专门食堂', GETDATE(), GETDATE()),
('留学生食堂', '主要为留学生提供餐饮服务的食堂', GETDATE(), GETDATE()),
('综合食堂', '为全校师生提供综合餐饮服务的食堂', GETDATE(), GETDATE());

-- 5. 插入校区数据（如果表不存在则创建）
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'tb_campus')
BEGIN
    CREATE TABLE tb_campus (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        name NVARCHAR(50) NOT NULL UNIQUE,
        description NVARCHAR(255) NULL,
        address NVARCHAR(255) NULL,
        create_time DATETIME2 NOT NULL DEFAULT GETDATE(),
        update_time DATETIME2 NOT NULL DEFAULT GETDATE()
    );
END

-- 清理并重新插入校区数据
DELETE FROM tb_campus;
DBCC CHECKIDENT ('tb_campus', RESEED, 0);

INSERT INTO tb_campus (name, description, create_time, update_time) VALUES
('北洋园校区', '天津大学北洋园校区', GETDATE(), GETDATE()),
('卫津路校区', '天津大学卫津路校区', GETDATE(), GETDATE()),
('滨海校区', '天津大学滨海校区', GETDATE(), GETDATE());

-- 6. 验证数据
PRINT '=== 数据验证结果 ===';

SELECT '角色数据' as 数据类型, COUNT(*) as 数量 FROM tb_role
UNION ALL
SELECT '管理员数据', COUNT(*) FROM tb_admin
UNION ALL
SELECT '窗口类型数据', COUNT(*) FROM tb_stall_type
UNION ALL
SELECT '食堂类型数据', COUNT(*) FROM tb_canteen_type
UNION ALL
SELECT '校区数据', COUNT(*) FROM tb_campus;

PRINT '=== 管理员状态验证 ===';
SELECT 
    a.id,
    a.username,
    a.name,
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

PRINT '=== 角色状态验证 ===';
SELECT 
    id,
    name,
    description,
    CASE 
        WHEN status = 0 THEN '启用'
        WHEN status = 1 THEN '禁用'
        ELSE '未知状态'
    END as status_desc,
    status as status_code,
    create_time,
    update_time
FROM tb_role
ORDER BY id;

PRINT '=== 修复完成 ===';
PRINT '状态字段逻辑已统一：0=启用/正常，1=禁用';
PRINT '前端显示逻辑已修复，与后端保持一致';
PRINT '所有基础数据已重新插入并验证';
