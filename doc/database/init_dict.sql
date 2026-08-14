-- ============================================================
-- 初始化字典数据（保管期限 / 密级 / 档案状态 / 借阅状态）
-- 适用数据库: document_management_system
-- 执行前提: 已执行 DocumentManagementSystemApplication.sql 建表
-- ============================================================

USE `document_management_system`;

-- 1. 字典类型
INSERT INTO `sys_dict` (`id`, `dict_code`, `dict_name`, `description`, `status`) VALUES
(1, 'retention_period', '保管期限', '档案保管期限：永久/长期/短期', 1),
(2, 'security_level',   '密级',     '档案密级：公开/内部/秘密/机密/绝密', 1),
(3, 'archive_status',   '档案状态', '档案生命周期状态', 1),
(4, 'borrow_status',    '借阅状态', '借阅申请状态', 1)
ON DUPLICATE KEY UPDATE `dict_name` = VALUES(`dict_name`);

-- 2. 字典项
INSERT INTO `sys_dict_item` (`dict_id`, `item_code`, `item_name`, `item_value`, `sort`, `status`) VALUES
-- 保管期限
(1, 'permanent', '永久', 'permanent', 1, 1),
(1, 'long_term', '长期', 'long_term', 2, 1),
(1, 'short_term', '短期', 'short_term', 3, 1),
-- 密级
(2, 'public',    '公开', 'public', 1, 1),
(2, 'internal',  '内部', 'internal', 2, 1),
(2, 'secret',    '秘密', 'secret', 3, 1),
(2, 'confidential', '机密', 'confidential', 4, 1),
(2, 'top_secret', '绝密', 'top_secret', 5, 1),
-- 档案状态
(3, 'arranging', '整理中', 1, 1, 1),
(3, 'archived',  '已归档', 2, 2, 1),
(3, 'sealed',    '已封库', 3, 3, 1),
(3, 'destroyed', '已销毁', 4, 4, 1),
-- 借阅状态
(4, 'pending',  '待审批', 1, 1, 1),
(4, 'approved', '已通过', 2, 2, 1),
(4, 'rejected', '已驳回', 3, 3, 1),
(4, 'returned', '已归还', 4, 4, 1);
