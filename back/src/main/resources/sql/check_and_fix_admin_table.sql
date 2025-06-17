-- ========================================
-- 检查和修复管理员表结构
-- 删除不需要的phone和email字段
-- ========================================

-- 1. 查看当前管理员表结构
PRINT '=== 当前管理员表结构 ===';
SELECT 
    COLUMN_NAME as 字段名,
    DATA_TYPE as 数据类型,
    CHARACTER_MAXIMUM_LENGTH as 最大长度,
    IS_NULLABLE as 允许NULL
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'tb_admin'
ORDER BY ORDINAL_POSITION;

-- 2. 删除phone字段（如果存在）
IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'tb_admin' AND COLUMN_NAME = 'phone')
BEGIN
    PRINT '删除phone字段...';
    ALTER TABLE tb_admin DROP COLUMN phone;
    PRINT 'phone字段已删除';
END
ELSE
BEGIN
    PRINT 'phone字段不存在，无需删除';
END

-- 3. 删除email字段（如果存在）
IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'tb_admin' AND COLUMN_NAME = 'email')
BEGIN
    PRINT '删除email字段...';
    ALTER TABLE tb_admin DROP COLUMN email;
    PRINT 'email字段已删除';
END
ELSE
BEGIN
    PRINT 'email字段不存在，无需删除';
END

-- 4. 查看修复后的管理员表结构
PRINT '=== 修复后的管理员表结构 ===';
SELECT 
    COLUMN_NAME as 字段名,
    DATA_TYPE as 数据类型,
    CHARACTER_MAXIMUM_LENGTH as 最大长度,
    IS_NULLABLE as 允许NULL
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'tb_admin'
ORDER BY ORDINAL_POSITION;

-- 5. 确保管理员状态正确
PRINT '=== 修复管理员状态 ===';
UPDATE tb_admin SET 
    status = 0,  -- 0=启用
    update_time = GETDATE()
WHERE status != 0;

-- 6. 确保角色数据存在
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

-- 7. 为管理员分配角色
PRINT '=== 更新管理员角色分配 ===';

UPDATE tb_admin SET 
    role_id = 1,
    status = 0,
    update_time = GETDATE()
WHERE username = 'sysadmin';

UPDATE tb_admin SET 
    role_id = 2,
    status = 0,
    update_time = GETDATE()
WHERE username = 'operationadmin';

UPDATE tb_admin SET 
    role_id = 3,
    status = 0,
    update_time = GETDATE()
WHERE username = 'contentadmin';

UPDATE tb_admin SET 
    role_id = 4,
    status = 0,
    update_time = GETDATE()
WHERE username = 'serviceadmin';

-- 8. 验证修复结果
PRINT '=== 验证修复结果 ===';

-- 查看管理员数据
PRINT '管理员数据：';
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

-- 9. 显示修复统计
DECLARE @adminCount INT, @roleCount INT;
SELECT @adminCount = COUNT(*) FROM tb_admin WHERE status = 0;
SELECT @roleCount = COUNT(*) FROM tb_role WHERE status = 0;

PRINT '=== 修复完成统计 ===';
PRINT '启用状态管理员数量: ' + CAST(@adminCount AS NVARCHAR(10));
PRINT '启用状态角色数量: ' + CAST(@roleCount AS NVARCHAR(10));
PRINT '修复完成！管理员表已清理phone和email字段，所有管理员现在都可以正常登录。';
