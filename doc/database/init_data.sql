-- ============================================================
-- 初始化数据：角色 + 账号（3 类角色）
-- 适用数据库: document_management_system
-- 密码: admin123（BCrypt 加密，与后端 BCrypt.checkpw 匹配）
-- 执行前提: 已执行 DocumentManagementSystemApplication.sql 建表
-- 角色菜单分配见 init_menu.sql
-- ============================================================

USE `document_management_system`;

-- 1. 初始化角色（3 类：超级管理员 / 档案管理员 / 普通用户）
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `description`, `status`, `sort`)
VALUES
(1, '超级管理员', 'super_admin',   '系统最高权限，拥有全部功能', 1, 0),
(2, '档案管理员', 'archive_admin', '负责档案业务（全宗/门类/案卷/档案）', 1, 1),
(3, '普通用户',   'common_user',   '档案检索利用与借阅', 1, 2)
ON DUPLICATE KEY UPDATE
    `role_name` = VALUES(`role_name`),
    `description` = VALUES(`description`),
    `status` = VALUES(`status`),
    `del_flag` = 0;

-- 2. 初始化管理员账号（admin / admin123）
--    BCrypt hash: $2a$10$1PZYwm470XOgzZ.Q3Y85Ru1gpoAlp3juuaMPLa.KQlXhb3jQUQInq
INSERT INTO `sys_user`
(`id`, `username`, `password`, `real_name`, `role_id`, `phone`, `email`, `status`)
VALUES (1, 'admin', '$2a$10$1PZYwm470XOgzZ.Q3Y85Ru1gpoAlp3juuaMPLa.KQlXhb3jQUQInq',
        '系统管理员', 1, '13800000000', 'admin@dms.local', 1)
ON DUPLICATE KEY UPDATE
    `password` = VALUES(`password`),
    `real_name` = VALUES(`real_name`),
    `role_id` = VALUES(`role_id`),
    `status` = VALUES(`status`),
    `del_flag` = 0;

-- 3. 档案管理员示例账号（zhangsan 张三丰 / admin123）
INSERT INTO `sys_user`
(`username`, `password`, `real_name`, `role_id`, `phone`, `email`, `status`)
VALUES ('zhangsan', '$2a$10$1PZYwm470XOgzZ.Q3Y85Ru1gpoAlp3juuaMPLa.KQlXhb3jQUQInq',
        '张三丰', 2, '13600000000', 'zhangsan@dms.local', 1)
ON DUPLICATE KEY UPDATE
    `password` = VALUES(`password`),
    `real_name` = VALUES(`real_name`),
    `role_id` = VALUES(`role_id`),
    `status` = VALUES(`status`),
    `del_flag` = 0;

-- 4. 普通用户示例账号（lisi 李四 / admin123）
INSERT INTO `sys_user`
(`username`, `password`, `real_name`, `role_id`, `phone`, `email`, `status`)
VALUES ('lisi', '$2a$10$1PZYwm470XOgzZ.Q3Y85Ru1gpoAlp3juuaMPLa.KQlXhb3jQUQInq',
        '李四', 3, '13700000000', 'lisi@dms.local', 1)
ON DUPLICATE KEY UPDATE
    `password` = VALUES(`password`),
    `real_name` = VALUES(`real_name`),
    `role_id` = VALUES(`role_id`),
    `status` = VALUES(`status`),
    `del_flag` = 0;
