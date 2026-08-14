-- ============================================================
-- 初始化菜单数据 + 超级管理员分配全部菜单
-- 适用数据库: document_management_system
-- 执行前提: 已执行 DocumentManagementSystemApplication.sql 建表 + init_data.sql 初始化角色/用户
-- ============================================================

USE `document_management_system`;

-- 1. 初始化菜单（目录/菜单/按钮三级）
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_title`, `menu_type`, `icon`, `path`, `component`, `perms`, `sort`, `status`) VALUES
-- 顶级目录
(1,  0, '系统管理', '系统管理', 1, 'Setting',      '/system', 'Layout',       NULL,                1, 1),
(2,  0, '档案管理', '档案管理', 1, 'FolderOpened', '/archive', 'Layout',       NULL,                2, 1),
-- 系统管理下的菜单
(3,  1, '用户管理', '用户管理', 2, 'User',         '/system/user',  'system/user/index',  NULL, 1, 1),
(4,  1, '角色管理', '角色管理', 2, 'UserFilled',   '/system/role',  'system/role/index',  NULL, 2, 1),
(5,  1, '菜单管理', '菜单管理', 2, 'Menu',         '/system/menu',  'system/menu/index',  NULL, 3, 1),
(6,  1, '字典管理', '字典管理', 2, 'Collection',   '/system/dict',  'system/dict/index',  NULL, 4, 1),
-- 档案管理下的菜单
(7,  2, '全宗管理', '全宗管理', 2, 'OfficeBuilding', '/archive/fonds', 'archive/fonds/index', NULL, 1, 1),
(8,  2, '门类管理', '门类管理', 2, 'Files',        '/archive/type',  'archive/type/index',  NULL, 2, 1),
(9,  2, '案卷管理', '案卷管理', 2, 'Folder',       '/archive/volume', 'archive/volume/index', NULL, 3, 1),
(10, 2, '档案管理', '档案管理', 2, 'Document',     '/archive/file',  'archive/file/index',  NULL, 4, 1),
-- 用户管理按钮权限
(11, 3, '用户新增', '新增用户', 3, NULL, NULL, NULL, 'system:user:add',            1, 1),
(12, 3, '用户编辑', '编辑用户', 3, NULL, NULL, NULL, 'system:user:edit',           2, 1),
(13, 3, '用户删除', '删除用户', 3, NULL, NULL, NULL, 'system:user:delete',         3, 1),
(14, 3, '用户状态', '启用禁用', 3, NULL, NULL, NULL, 'system:user:status',         4, 1),
(15, 3, '重置密码', '重置密码', 3, NULL, NULL, NULL, 'system:user:reset-password', 5, 1),
-- 角色管理按钮权限
(16, 4, '角色新增', '新增角色', 3, NULL, NULL, NULL, 'system:role:add',   1, 1),
(17, 4, '角色编辑', '编辑角色', 3, NULL, NULL, NULL, 'system:role:edit',  2, 1),
(18, 4, '角色删除', '删除角色', 3, NULL, NULL, NULL, 'system:role:delete', 3, 1),
-- 菜单管理按钮权限
(19, 5, '菜单新增', '新增菜单', 3, NULL, NULL, NULL, 'system:menu:add',   1, 1),
(20, 5, '菜单编辑', '编辑菜单', 3, NULL, NULL, NULL, 'system:menu:edit',  2, 1),
(21, 5, '菜单删除', '删除菜单', 3, NULL, NULL, NULL, 'system:menu:delete', 3, 1),
-- 字典管理按钮权限
(22, 6, '字典新增', '新增字典', 3, NULL, NULL, NULL, 'system:dict:add',    1, 1),
(23, 6, '字典编辑', '编辑字典', 3, NULL, NULL, NULL, 'system:dict:edit',   2, 1),
(24, 6, '字典删除', '删除字典', 3, NULL, NULL, NULL, 'system:dict:delete', 3, 1)
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`);

-- 2. 超级管理员（role_id=1）分配全部菜单
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, `id` FROM `sys_menu`
ON DUPLICATE KEY UPDATE `role_id` = `role_id`;
