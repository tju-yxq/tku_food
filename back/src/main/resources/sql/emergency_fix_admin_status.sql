-- ========================================
-- 紧急修复管理员状态问题
-- 立即将所有管理员状态设置为启用（0）
-- ========================================

-- 1. 查看当前管理员状态
PRINT '=== 修复前的管理员状态 ===';
SELECT 
    id,
    username,
    name,
    CASE 
        WHEN status = 0 THEN '启用'
        WHEN status = 1 THEN '禁用'
        ELSE '未知状态'
    END as status_desc,
    status as status_code,
    role_id
FROM tb_admin
ORDER BY id;

-- 2. 紧急修复：将所有管理员状态设置为启用（0）
UPDATE tb_admin SET 
    status = 0,  -- 0=启用
    update_time = GETDATE()
WHERE status != 0;  -- 只更新非启用状态的管理员

-- 3. 确保角色数据存在
-- 如果角色表为空，插入基础角色数据
IF NOT EXISTS (SELECT 1 FROM tb_role)
BEGIN
    PRINT '插入基础角色数据...';
    INSERT INTO tb_role (name, description, status, create_time, update_time) VALUES
    ('超级管理员', '拥有系统所有权限，负责系统整体管理和维护', 0, GETDATE(), GETDATE()),
    ('运营管理员', '负责日常运营管理，包括内容审核、用户管理等', 0, GETDATE(), GETDATE()),
    ('内容管理员', '负责管理食堂、窗口、菜品等核心基础数据', 0, GETDATE(), GETDATE()),
    ('客服管理员', '负责处理用户反馈、投诉建议等客服相关工作', 0, GETDATE(), GETDATE());
END

-- 4. 为管理员分配角色（如果没有角色）
UPDATE tb_admin SET 
    role_id = 1,
    update_time = GETDATE()
WHERE username = 'sysadmin' AND (role_id IS NULL OR role_id = 0);

UPDATE tb_admin SET 
    role_id = 2,
    update_time = GETDATE()
WHERE username = 'operationadmin' AND (role_id IS NULL OR role_id = 0);

UPDATE tb_admin SET 
    role_id = 3,
    update_time = GETDATE()
WHERE username = 'contentadmin' AND (role_id IS NULL OR role_id = 0);

UPDATE tb_admin SET 
    role_id = 4,
    update_time = GETDATE()
WHERE username = 'serviceadmin' AND (role_id IS NULL OR role_id = 0);

-- 5. 验证修复结果
PRINT '=== 修复后的管理员状态 ===';
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

PRINT '=== 角色列表 ===';
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

-- 6. 显示修复统计
DECLARE @updatedCount INT;
SELECT @updatedCount = COUNT(*) FROM tb_admin WHERE status = 0;

PRINT '=== 修复完成统计 ===';
PRINT '总管理员数量: ' + CAST((SELECT COUNT(*) FROM tb_admin) AS NVARCHAR(10));
PRINT '启用状态管理员数量: ' + CAST(@updatedCount AS NVARCHAR(10));
PRINT '角色数量: ' + CAST((SELECT COUNT(*) FROM tb_role) AS NVARCHAR(10));
PRINT '修复完成！所有管理员现在都可以正常登录。';
