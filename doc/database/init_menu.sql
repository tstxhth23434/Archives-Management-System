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
(24, 6, '字典删除', '删除字典', 3, NULL, NULL, NULL, 'system:dict:delete', 3, 1),
-- 查询按钮权限（D5 权限收紧：读接口也受 @SaCheckPermission 控制）
(25, 3, '用户查询', '查询用户', 3, NULL, NULL, NULL, 'system:user:query', 6, 1),
(26, 4, '角色查询', '查询角色', 3, NULL, NULL, NULL, 'system:role:query', 4, 1),
(27, 5, '菜单查询', '查询菜单', 3, NULL, NULL, NULL, 'system:menu:query', 4, 1),
(28, 6, '字典查询', '查询字典', 3, NULL, NULL, NULL, 'system:dict:query', 4, 1),
-- 操作日志菜单 + 查询按钮
(29, 1, '操作日志', '操作日志', 2, 'Tickets', '/system/log', 'system/log/index', NULL, 5, 1),
(30, 29, '日志查询', '查询日志', 3, NULL, NULL, NULL, 'system:log:query', 1, 1),
-- 档案按钮权限（D8 全宗/门类）
(31, 7, '全宗查询', '查询全宗', 3, NULL, NULL, NULL, 'archive:fonds:query', 1, 1),
(32, 7, '全宗新增', '新增全宗', 3, NULL, NULL, NULL, 'archive:fonds:add', 2, 1),
(33, 7, '全宗编辑', '编辑全宗', 3, NULL, NULL, NULL, 'archive:fonds:edit', 3, 1),
(34, 7, '全宗删除', '删除全宗', 3, NULL, NULL, NULL, 'archive:fonds:delete', 4, 1),
(35, 8, '门类查询', '查询门类', 3, NULL, NULL, NULL, 'archive:type:query', 1, 1),
(36, 8, '门类新增', '新增门类', 3, NULL, NULL, NULL, 'archive:type:add', 2, 1),
(37, 8, '门类编辑', '编辑门类', 3, NULL, NULL, NULL, 'archive:type:edit', 3, 1),
(38, 8, '门类删除', '删除门类', 3, NULL, NULL, NULL, 'archive:type:delete', 4, 1),
-- 档案浏览页 + 案卷查询按钮（D9 档案树）
(39, 2, '档案浏览', '档案浏览', 2, 'Files', '/archive/overview', 'archive/overview/index', NULL, 0, 1),
(40, 39, '案卷查询', '查询案卷', 3, NULL, NULL, NULL, 'archive:volume:query', 1, 1),
-- 首页/驾驶舱（顶级，所有角色可见）
(48, 0, '首页', '首页', 2, 'HomeFilled', '/dashboard', 'dashboard/index', NULL, 0, 1),
-- 档案检索（D16，所有角色可见）
(49, 0, '档案检索', '档案检索', 2, 'Search', '/archive/search', 'archive/search/index', NULL, 1, 1),
(50, 49, '检索查询', '检索查询', 3, NULL, NULL, NULL, 'archive:search:query', 1, 1),
-- 借阅（D17 申请 / D18 审批）
(51, 0, '我的借阅', '我的借阅', 2, 'Reading', '/archive/borrow', 'archive/borrow/index', NULL, 2, 1),
(52, 51, '借阅申请', '申请借阅', 3, NULL, NULL, NULL, 'archive:borrow:apply', 1, 1),
(53, 51, '借阅审批', '借阅审批', 3, NULL, NULL, NULL, 'archive:borrow:approve', 2, 1),
-- 案卷/文件按钮权限（D10 档号生成 + 著录）
(41, 9,  '案卷新增', '新增案卷', 3, NULL, NULL, NULL, 'archive:volume:add', 1, 1),
(42, 9,  '案卷编辑', '编辑案卷', 3, NULL, NULL, NULL, 'archive:volume:edit', 2, 1),
(43, 9,  '案卷删除', '删除案卷', 3, NULL, NULL, NULL, 'archive:volume:delete', 3, 1),
(44, 10, '文件查询', '查询文件', 3, NULL, NULL, NULL, 'archive:file:query', 1, 1),
(45, 10, '文件新增', '新增文件', 3, NULL, NULL, NULL, 'archive:file:add', 2, 1),
(46, 10, '文件编辑', '编辑文件', 3, NULL, NULL, NULL, 'archive:file:edit', 3, 1),
(47, 10, '文件删除', '删除文件', 3, NULL, NULL, NULL, 'archive:file:delete', 4, 1)
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`);

-- 2. 角色菜单分配
-- 超级管理员（role_id=1）：全部菜单
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, `id` FROM `sys_menu`
ON DUPLICATE KEY UPDATE `role_id` = `role_id`;

-- 档案管理员（role_id=2）：档案管理全部菜单 + 按钮权限 + 档案浏览
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 2, `id` FROM `sys_menu` WHERE `id` IN (2, 7, 8, 9, 10, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47)
ON DUPLICATE KEY UPDATE `role_id` = `role_id`;

-- 普通用户（role_id=3）：首页 + 档案检索 + 我的借阅/借阅申请（审批 D18 仅管理员）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 3, `id` FROM `sys_menu` WHERE `id` IN (48, 49, 50, 51, 52)
ON DUPLICATE KEY UPDATE `role_id` = `role_id`;
