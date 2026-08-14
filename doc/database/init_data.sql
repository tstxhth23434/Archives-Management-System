-- ============================================================
-- 初始化数据：管理员角色 + 管理员账号
-- 适用数据库: document_management_system
-- 密码: admin123（BCrypt 加密，与后端 BCrypt.checkpw 匹配）
-- 执行前提: 已执行 DocumentManagementSystemApplication.sql 建表
-- ============================================================

USE `document_management_system`;

-- 1. 初始化角色（超级管理员）
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `description`, `status`, `sort`)
VALUES (1, '超级管理员', 'super_admin', '系统最高权限，拥有全部功能', 1, 0)
ON DUPLICATE KEY UPDATE `role_name` = VALUES(`role_name`);

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
    `status` = VALUES(`status`);
