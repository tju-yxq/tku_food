-- ========================================
-- 修复平台设置管理模块的基础数据
-- ========================================

-- 1. 插入角色数据（如果不存在）
IF NOT EXISTS (SELECT 1 FROM tb_role WHERE id = 1)
BEGIN
    INSERT INTO tb_role (id, name, description, status, create_time, update_time) VALUES
    (1, '超级管理员', '拥有系统所有权限，负责系统整体管理和维护', 0, GETDATE(), GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM tb_role WHERE id = 2)
BEGIN
    INSERT INTO tb_role (id, name, description, status, create_time, update_time) VALUES
    (2, '运营管理员', '负责日常运营管理，包括内容审核、用户管理等', 0, GETDATE(), GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM tb_role WHERE id = 3)
BEGIN
    INSERT INTO tb_role (id, name, description, status, create_time, update_time) VALUES
    (3, '内容管理员', '负责管理食堂、窗口、菜品等核心基础数据', 0, GETDATE(), GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM tb_role WHERE id = 4)
BEGIN
    INSERT INTO tb_role (id, name, description, status, create_time, update_time) VALUES
    (4, '客服管理员', '负责处理用户反馈、投诉建议等客服相关工作', 0, GETDATE(), GETDATE());
END

-- 2. 更新管理员的角色分配和状态
UPDATE tb_admin SET 
    role_id = 1,
    status = 1,
    update_time = GETDATE()
WHERE username = 'sysadmin';

UPDATE tb_admin SET 
    role_id = 2,
    status = 1,
    update_time = GETDATE()
WHERE username = 'operationadmin';

UPDATE tb_admin SET 
    role_id = 3,
    status = 1,
    update_time = GETDATE()
WHERE username = 'contentadmin';

UPDATE tb_admin SET 
    role_id = 4,
    status = 1,
    update_time = GETDATE()
WHERE username = 'serviceadmin';

-- 3. 插入窗口类型数据（如果不存在）
IF NOT EXISTS (SELECT 1 FROM tb_stall_type WHERE id = 1)
BEGIN
    INSERT INTO tb_stall_type (id, name, description, create_time, update_time) VALUES
    (1, '主食窗口', '提供米饭、面条等主食类食品', GETDATE(), GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM tb_stall_type WHERE id = 2)
BEGIN
    INSERT INTO tb_stall_type (id, name, description, create_time, update_time) VALUES
    (2, '面食窗口', '提供各类面条、饺子、包子等面食', GETDATE(), GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM tb_stall_type WHERE id = 3)
BEGIN
    INSERT INTO tb_stall_type (id, name, description, create_time, update_time) VALUES
    (3, '快餐窗口', '提供快餐、盖浇饭等快速餐食', GETDATE(), GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM tb_stall_type WHERE id = 4)
BEGIN
    INSERT INTO tb_stall_type (id, name, description, create_time, update_time) VALUES
    (4, '特色窗口', '提供地方特色菜、民族菜等特色餐食', GETDATE(), GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM tb_stall_type WHERE id = 5)
BEGIN
    INSERT INTO tb_stall_type (id, name, description, create_time, update_time) VALUES
    (5, '小食窗口', '提供小食、零食、饮品等轻食', GETDATE(), GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM tb_stall_type WHERE id = 6)
BEGIN
    INSERT INTO tb_stall_type (id, name, description, create_time, update_time) VALUES
    (6, '清真窗口', '提供符合清真标准的餐食', GETDATE(), GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM tb_stall_type WHERE id = 7)
BEGIN
    INSERT INTO tb_stall_type (id, name, description, create_time, update_time) VALUES
    (7, '素食窗口', '提供素食、健康餐等特殊饮食需求', GETDATE(), GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM tb_stall_type WHERE id = 8)
BEGIN
    INSERT INTO tb_stall_type (id, name, description, create_time, update_time) VALUES
    (8, '自选窗口', '提供自选菜品、自助餐等形式', GETDATE(), GETDATE());
END

-- 4. 插入食堂类型数据（如果不存在）
IF NOT EXISTS (SELECT 1 FROM tb_canteen_type WHERE id = 1)
BEGIN
    INSERT INTO tb_canteen_type (id, name, description, create_time, update_time) VALUES
    (1, '学生食堂', '主要为学生提供餐饮服务的食堂', GETDATE(), GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM tb_canteen_type WHERE id = 2)
BEGIN
    INSERT INTO tb_canteen_type (id, name, description, create_time, update_time) VALUES
    (2, '教工食堂', '主要为教职工提供餐饮服务的食堂', GETDATE(), GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM tb_canteen_type WHERE id = 3)
BEGIN
    INSERT INTO tb_canteen_type (id, name, description, create_time, update_time) VALUES
    (3, '清真食堂', '提供清真餐饮的专门食堂', GETDATE(), GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM tb_canteen_type WHERE id = 4)
BEGIN
    INSERT INTO tb_canteen_type (id, name, description, create_time, update_time) VALUES
    (4, '留学生食堂', '主要为留学生提供餐饮服务的食堂', GETDATE(), GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM tb_canteen_type WHERE id = 5)
BEGIN
    INSERT INTO tb_canteen_type (id, name, description, create_time, update_time) VALUES
    (5, '综合食堂', '为全校师生提供综合餐饮服务的食堂', GETDATE(), GETDATE());
END

-- 5. 插入校区数据（如果不存在）
IF NOT EXISTS (SELECT 1 FROM tb_campus WHERE id = 1)
BEGIN
    INSERT INTO tb_campus (id, name, description, create_time, update_time) VALUES
    (1, '北洋园校区', '天津大学北洋园校区', GETDATE(), GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM tb_campus WHERE id = 2)
BEGIN
    INSERT INTO tb_campus (id, name, description, create_time, update_time) VALUES
    (2, '卫津路校区', '天津大学卫津路校区', GETDATE(), GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM tb_campus WHERE id = 3)
BEGIN
    INSERT INTO tb_campus (id, name, description, create_time, update_time) VALUES
    (3, '滨海校区', '天津大学滨海校区', GETDATE(), GETDATE());
END

-- 6. 查询验证数据
SELECT '角色数据' as 数据类型, COUNT(*) as 数量 FROM tb_role
UNION ALL
SELECT '管理员数据', COUNT(*) FROM tb_admin
UNION ALL
SELECT '窗口类型数据', COUNT(*) FROM tb_stall_type
UNION ALL
SELECT '食堂类型数据', COUNT(*) FROM tb_canteen_type
UNION ALL
SELECT '校区数据', COUNT(*) FROM tb_campus;

-- 7. 查看管理员角色分配情况
SELECT 
    a.id,
    a.username,
    a.name,
    r.name as role_name,
    a.status,
    a.role_id
FROM tb_admin a
LEFT JOIN tb_role r ON a.role_id = r.id
ORDER BY a.id;
