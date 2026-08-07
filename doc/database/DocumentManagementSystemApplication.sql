-- ============================================================
-- 数据库名称: DocumentManagementSystemApplication
-- 项目名称: 档案管理系统（低配版 / 毕业设计版）
-- 技术栈: Spring Boot + MyBatis-Plus + Sa-Token + MySQL 8.0
-- 设计原则:
--   1. 保留档案行业业务骨架: 全宗 -> 门类 -> 案卷 -> 文件
--   2. 档案全生命周期闭环: 采集 -> 整理 -> 归档 -> 封库 -> 借阅 -> 鉴定 -> 销毁
--   3. 逻辑删除 + 操作留痕, 符合档案行业合规要求
--   4. 所有表统一公共字段: create_by, create_time, update_by, update_time, del_flag
-- ============================================================

CREATE DATABASE IF NOT EXISTS `DocumentManagementSystemApplication`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE `DocumentManagementSystemApplication`;

-- ============================================================
-- 一、系统管理域
-- ============================================================

-- 1. 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id`              BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`        VARCHAR(50)         NOT NULL COMMENT '登录账号',
    `password`        VARCHAR(100)        NOT NULL COMMENT '登录密码(BCrypt加密)',
    `real_name`       VARCHAR(50)         DEFAULT NULL COMMENT '真实姓名',
    `role_id`         BIGINT UNSIGNED     DEFAULT NULL COMMENT '角色ID',
    `fonds_id`        BIGINT UNSIGNED     DEFAULT NULL COMMENT '所属全宗ID(数据权限用)',
    `phone`           VARCHAR(20)         DEFAULT NULL COMMENT '手机号',
    `email`           VARCHAR(100)        DEFAULT NULL COMMENT '邮箱',
    `avatar`          VARCHAR(255)        DEFAULT NULL COMMENT '头像地址',
    `status`          TINYINT UNSIGNED    DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    `login_ip`        VARCHAR(50)         DEFAULT NULL COMMENT '最后登录IP',
    `login_time`      DATETIME            DEFAULT NULL COMMENT '最后登录时间',
    `create_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '创建者ID',
    `create_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '更新者ID',
    `update_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`        TINYINT UNSIGNED    DEFAULT 0 COMMENT '删除标志: 0正常 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_user_username` (`username`),
    KEY `idx_sys_user_role_id` (`role_id`),
    KEY `idx_sys_user_fonds_id` (`fonds_id`),
    KEY `idx_sys_user_status` (`status`),
    KEY `idx_sys_user_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- 2. 角色表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id`              BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name`       VARCHAR(50)         NOT NULL COMMENT '角色名称',
    `role_code`       VARCHAR(50)         NOT NULL COMMENT '角色编码',
    `description`     VARCHAR(200)        DEFAULT NULL COMMENT '角色描述',
    `status`          TINYINT UNSIGNED    DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    `sort`            INT UNSIGNED        DEFAULT 0 COMMENT '显示排序',
    `create_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '创建者ID',
    `create_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '更新者ID',
    `update_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`        TINYINT UNSIGNED    DEFAULT 0 COMMENT '删除标志: 0正常 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_role_role_code` (`role_code`),
    KEY `idx_sys_role_status` (`status`),
    KEY `idx_sys_role_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色表';

-- 3. 菜单/权限表
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
    `id`              BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `parent_id`       BIGINT UNSIGNED     DEFAULT 0 COMMENT '父菜单ID, 0为顶级',
    `menu_name`       VARCHAR(50)         NOT NULL COMMENT '菜单名称',
    `menu_title`      VARCHAR(50)         DEFAULT NULL COMMENT '菜单标题(前端显示)',
    `menu_type`       TINYINT UNSIGNED    DEFAULT 1 COMMENT '菜单类型: 1目录 2菜单 3按钮',
    `icon`            VARCHAR(100)        DEFAULT NULL COMMENT '菜单图标',
    `path`            VARCHAR(200)        DEFAULT NULL COMMENT '路由路径',
    `component`       VARCHAR(200)        DEFAULT NULL COMMENT '组件路径',
    `perms`           VARCHAR(100)        DEFAULT NULL COMMENT '权限标识, 如: archive:file:list',
    `sort`            INT UNSIGNED        DEFAULT 0 COMMENT '显示排序',
    `status`          TINYINT UNSIGNED    DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    `create_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '创建者ID',
    `create_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '更新者ID',
    `update_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`        TINYINT UNSIGNED    DEFAULT 0 COMMENT '删除标志: 0正常 1已删除',
    PRIMARY KEY (`id`),
    KEY `idx_sys_menu_parent_id` (`parent_id`),
    KEY `idx_sys_menu_menu_type` (`menu_type`),
    KEY `idx_sys_menu_status` (`status`),
    KEY `idx_sys_menu_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统菜单/权限表';

-- 4. 角色-菜单关联表
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
    `id`              BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `role_id`         BIGINT UNSIGNED     NOT NULL COMMENT '角色ID',
    `menu_id`         BIGINT UNSIGNED     NOT NULL COMMENT '菜单ID',
    `create_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_role_menu_role_menu` (`role_id`, `menu_id`),
    KEY `idx_sys_role_menu_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';

-- 5. 字典类型表
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict` (
    `id`              BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '字典ID',
    `dict_code`       VARCHAR(50)         NOT NULL COMMENT '字典编码, 如: retention_period',
    `dict_name`       VARCHAR(50)         NOT NULL COMMENT '字典名称, 如: 保管期限',
    `description`     VARCHAR(200)        DEFAULT NULL COMMENT '字典描述',
    `status`          TINYINT UNSIGNED    DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    `create_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '创建者ID',
    `create_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '更新者ID',
    `update_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`        TINYINT UNSIGNED    DEFAULT 0 COMMENT '删除标志: 0正常 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_dict_dict_code` (`dict_code`),
    KEY `idx_sys_dict_status` (`status`),
    KEY `idx_sys_dict_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统字典类型表';

-- 6. 字典项表
DROP TABLE IF EXISTS `sys_dict_item`;
CREATE TABLE `sys_dict_item` (
    `id`              BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '字典项ID',
    `dict_id`         BIGINT UNSIGNED     NOT NULL COMMENT '字典类型ID',
    `item_code`       VARCHAR(50)         NOT NULL COMMENT '字典项编码',
    `item_name`       VARCHAR(50)         NOT NULL COMMENT '字典项名称',
    `item_value`      VARCHAR(100)        DEFAULT NULL COMMENT '字典项值',
    `sort`            INT UNSIGNED        DEFAULT 0 COMMENT '显示排序',
    `status`          TINYINT UNSIGNED    DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    `create_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '创建者ID',
    `create_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '更新者ID',
    `update_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`        TINYINT UNSIGNED    DEFAULT 0 COMMENT '删除标志: 0正常 1已删除',
    PRIMARY KEY (`id`),
    KEY `idx_sys_dict_item_dict_id` (`dict_id`),
    KEY `idx_sys_dict_item_status` (`status`),
    KEY `idx_sys_dict_item_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统字典项表';

-- 7. 操作日志表
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
    `id`              BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `user_id`         BIGINT UNSIGNED     DEFAULT NULL COMMENT '操作用户ID',
    `username`        VARCHAR(50)         DEFAULT NULL COMMENT '操作用户账号',
    `operation`       VARCHAR(100)        DEFAULT NULL COMMENT '操作描述',
    `method`          VARCHAR(500)        DEFAULT NULL COMMENT '请求方法(类名.方法名)',
    `params`          TEXT                DEFAULT NULL COMMENT '请求参数(JSON)',
    `ip`              VARCHAR(50)         DEFAULT NULL COMMENT '操作IP',
    `spend_time`      INT UNSIGNED        DEFAULT 0 COMMENT '耗时(毫秒)',
    `create_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_sys_log_user_id` (`user_id`),
    KEY `idx_sys_log_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统操作日志表';

-- ============================================================
-- 二、档案核心域
-- ============================================================

-- 8. 全宗表
DROP TABLE IF EXISTS `da_fonds`;
CREATE TABLE `da_fonds` (
    `id`              BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '全宗ID',
    `fonds_code`      VARCHAR(50)         NOT NULL COMMENT '全宗号, 如: JSXY',
    `fonds_name`      VARCHAR(100)        NOT NULL COMMENT '全宗名称, 如: 计算机学院',
    `description`     VARCHAR(500)        DEFAULT NULL COMMENT '全宗描述',
    `status`          TINYINT UNSIGNED    DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    `sort`            INT UNSIGNED        DEFAULT 0 COMMENT '显示排序',
    `create_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '创建者ID',
    `create_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '更新者ID',
    `update_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`        TINYINT UNSIGNED    DEFAULT 0 COMMENT '删除标志: 0正常 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_da_fonds_fonds_code` (`fonds_code`),
    KEY `idx_da_fonds_status` (`status`),
    KEY `idx_da_fonds_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='档案全宗表';

-- 9. 档案门类表
DROP TABLE IF EXISTS `da_archive_type`;
CREATE TABLE `da_archive_type` (
    `id`              BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '门类ID',
    `type_code`       VARCHAR(50)         NOT NULL COMMENT '门类代码, 如: WS',
    `type_name`       VARCHAR(100)        NOT NULL COMMENT '门类名称, 如: 文书档案',
    `fonds_id`        BIGINT UNSIGNED     NOT NULL COMMENT '所属全宗ID',
    `description`     VARCHAR(500)        DEFAULT NULL COMMENT '门类描述',
    `retention_period` VARCHAR(20)        DEFAULT NULL COMMENT '默认保管期限(字典值)',
    `sort`            INT UNSIGNED        DEFAULT 0 COMMENT '显示排序',
    `status`          TINYINT UNSIGNED    DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    `create_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '创建者ID',
    `create_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '更新者ID',
    `update_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`        TINYINT UNSIGNED    DEFAULT 0 COMMENT '删除标志: 0正常 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_da_archive_type_code_fonds` (`fonds_id`, `type_code`),
    KEY `idx_da_archive_type_fonds_id` (`fonds_id`),
    KEY `idx_da_archive_type_status` (`status`),
    KEY `idx_da_archive_type_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='档案门类表';

-- 10. 案卷表
DROP TABLE IF EXISTS `da_volume`;
CREATE TABLE `da_volume` (
    `id`              BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '案卷ID',
    `fonds_id`        BIGINT UNSIGNED     NOT NULL COMMENT '所属全宗ID',
    `type_id`         BIGINT UNSIGNED     NOT NULL COMMENT '所属门类ID',
    `volume_no`       VARCHAR(50)         NOT NULL COMMENT '案卷号',
    `title`           VARCHAR(200)        NOT NULL COMMENT '案卷题名',
    `year`            INT UNSIGNED        DEFAULT NULL COMMENT '年度',
    `retention_period` VARCHAR(20)        DEFAULT NULL COMMENT '保管期限(字典值)',
    `security_level`  VARCHAR(20)         DEFAULT NULL COMMENT '密级(字典值)',
    `status`          TINYINT UNSIGNED    DEFAULT 1 COMMENT '状态: 1整理中 2已归档 3已封库',
    `create_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '创建者ID',
    `create_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '更新者ID',
    `update_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`        TINYINT UNSIGNED    DEFAULT 0 COMMENT '删除标志: 0正常 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_da_volume_volume_no` (`volume_no`),
    KEY `idx_da_volume_fonds_id` (`fonds_id`),
    KEY `idx_da_volume_type_id` (`type_id`),
    KEY `idx_da_volume_year` (`year`),
    KEY `idx_da_volume_status` (`status`),
    KEY `idx_da_volume_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='档案案卷表';

-- 11. 文件/档案表
DROP TABLE IF EXISTS `da_file`;
CREATE TABLE `da_file` (
    `id`              BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '档案ID',
    `volume_id`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '所属案卷ID(未组卷可为空)',
    `archive_no`      VARCHAR(100)        NOT NULL COMMENT '档号, 如: JSXY-WS-2026-0001',
    `title`           VARCHAR(200)        NOT NULL COMMENT '题名',
    `author`          VARCHAR(100)        DEFAULT NULL COMMENT '责任者',
    `doc_date`        DATE                DEFAULT NULL COMMENT '文件日期',
    `year`            INT UNSIGNED        DEFAULT NULL COMMENT '年度',
    `retention_period` VARCHAR(20)        DEFAULT NULL COMMENT '保管期限(字典值)',
    `security_level`  VARCHAR(20)         DEFAULT NULL COMMENT '密级(字典值)',
    `keywords`        VARCHAR(500)        DEFAULT NULL COMMENT '关键词, 逗号分隔',
    `pages`           INT UNSIGNED        DEFAULT 0 COMMENT '页数',
    `summary`         TEXT                DEFAULT NULL COMMENT '摘要/备注',
    `status`          TINYINT UNSIGNED    DEFAULT 1 COMMENT '状态: 1整理中 2已归档 3已封库 4已销毁',
    `fonds_id`        BIGINT UNSIGNED     NOT NULL COMMENT '所属全宗ID',
    `type_id`         BIGINT UNSIGNED     NOT NULL COMMENT '所属门类ID',
    `warehouse_id`    BIGINT UNSIGNED     DEFAULT NULL COMMENT '存放库房ID(可选)',
    `shelf_id`        BIGINT UNSIGNED     DEFAULT NULL COMMENT '存放密集架ID(可选)',
    `cell_id`         BIGINT UNSIGNED     DEFAULT NULL COMMENT '存放单元格ID(可选)',
    `create_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '创建者ID',
    `create_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '更新者ID',
    `update_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`        TINYINT UNSIGNED    DEFAULT 0 COMMENT '删除标志: 0正常 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_da_file_archive_no` (`archive_no`),
    KEY `idx_da_file_volume_id` (`volume_id`),
    KEY `idx_da_file_fonds_id` (`fonds_id`),
    KEY `idx_da_file_type_id` (`type_id`),
    KEY `idx_da_file_year` (`year`),
    KEY `idx_da_file_status` (`status`),
    KEY `idx_da_file_doc_date` (`doc_date`),
    KEY `idx_da_file_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='档案文件表';

-- 12. 电子原文表
DROP TABLE IF EXISTS `da_electronic_file`;
CREATE TABLE `da_electronic_file` (
    `id`              BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '电子原文ID',
    `archive_id`      BIGINT UNSIGNED     NOT NULL COMMENT '所属档案ID',
    `file_name`       VARCHAR(255)        NOT NULL COMMENT '原始文件名',
    `file_path`       VARCHAR(500)        NOT NULL COMMENT '存储路径',
    `file_size`       BIGINT UNSIGNED     DEFAULT 0 COMMENT '文件大小(字节)',
    `file_type`       VARCHAR(50)         DEFAULT NULL COMMENT '文件MIME类型',
    `file_suffix`     VARCHAR(20)         DEFAULT NULL COMMENT '文件后缀',
    `is_original`     TINYINT UNSIGNED    DEFAULT 1 COMMENT '是否原件: 0否 1是',
    `sort`            INT UNSIGNED        DEFAULT 0 COMMENT '排序',
    `upload_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    `create_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '创建者ID',
    `create_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '更新者ID',
    `update_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`        TINYINT UNSIGNED    DEFAULT 0 COMMENT '删除标志: 0正常 1已删除',
    PRIMARY KEY (`id`),
    KEY `idx_da_electronic_file_archive_id` (`archive_id`),
    KEY `idx_da_electronic_file_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='档案电子原文表';

-- 13. 生命周期履历表
DROP TABLE IF EXISTS `da_lifecycle`;
CREATE TABLE `da_lifecycle` (
    `id`              BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '履历ID',
    `archive_id`      BIGINT UNSIGNED     NOT NULL COMMENT '所属档案ID',
    `action`          VARCHAR(50)         NOT NULL COMMENT '动作编码, 如: ARCHIVE, SEAL, BORROW, RETURN, APPRAISAL, DESTROY',
    `action_name`     VARCHAR(50)         NOT NULL COMMENT '动作名称, 如: 归档, 封库, 借阅, 归还',
    `operator_id`     BIGINT UNSIGNED     DEFAULT NULL COMMENT '操作人ID',
    `operator_name`   VARCHAR(50)         DEFAULT NULL COMMENT '操作人姓名',
    `detail`          VARCHAR(1000)       DEFAULT NULL COMMENT '操作详情',
    `create_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_da_lifecycle_archive_id` (`archive_id`),
    KEY `idx_da_lifecycle_action` (`action`),
    KEY `idx_da_lifecycle_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='档案生命周期履历表';

-- ============================================================
-- 三、业务流转域
-- ============================================================

-- 14. 借阅单表
DROP TABLE IF EXISTS `da_borrow`;
CREATE TABLE `da_borrow` (
    `id`              BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '借阅单ID',
    `borrow_no`       VARCHAR(50)         NOT NULL COMMENT '借阅单号',
    `archive_id`      BIGINT UNSIGNED     NOT NULL COMMENT '借阅档案ID',
    `applicant_id`    BIGINT UNSIGNED     NOT NULL COMMENT '申请人ID',
    `applicant_name`  VARCHAR(50)         DEFAULT NULL COMMENT '申请人姓名',
    `reason`          VARCHAR(500)        DEFAULT NULL COMMENT '借阅理由',
    `apply_time`      DATETIME            DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `status`          TINYINT UNSIGNED    DEFAULT 1 COMMENT '状态: 1待审批 2已通过 3已驳回 4已归还',
    `approver_id`     BIGINT UNSIGNED     DEFAULT NULL COMMENT '审批人ID',
    `approver_name`   VARCHAR(50)         DEFAULT NULL COMMENT '审批人姓名',
    `approve_time`    DATETIME            DEFAULT NULL COMMENT '审批时间',
    `approve_comment` VARCHAR(500)        DEFAULT NULL COMMENT '审批意见',
    `return_time`     DATETIME            DEFAULT NULL COMMENT '归还时间',
    `create_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '创建者ID',
    `create_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '更新者ID',
    `update_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`        TINYINT UNSIGNED    DEFAULT 0 COMMENT '删除标志: 0正常 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_da_borrow_borrow_no` (`borrow_no`),
    KEY `idx_da_borrow_archive_id` (`archive_id`),
    KEY `idx_da_borrow_applicant_id` (`applicant_id`),
    KEY `idx_da_borrow_status` (`status`),
    KEY `idx_da_borrow_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='档案借阅单表';

-- 15. 鉴定记录表
DROP TABLE IF EXISTS `da_appraisal`;
CREATE TABLE `da_appraisal` (
    `id`              BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '鉴定记录ID',
    `archive_id`      BIGINT UNSIGNED     NOT NULL COMMENT '鉴定档案ID',
    `appraisal_no`    VARCHAR(50)         DEFAULT NULL COMMENT '鉴定单号',
    `result`          VARCHAR(20)         NOT NULL COMMENT '鉴定结果: 续存/销毁/移交',
    `reason`          VARCHAR(500)        DEFAULT NULL COMMENT '鉴定理由',
    `operator_id`     BIGINT UNSIGNED     DEFAULT NULL COMMENT '鉴定人ID',
    `operator_name`   VARCHAR(50)         DEFAULT NULL COMMENT '鉴定人姓名',
    `create_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '创建者ID',
    `create_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '更新者ID',
    `update_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`        TINYINT UNSIGNED    DEFAULT 0 COMMENT '删除标志: 0正常 1已删除',
    PRIMARY KEY (`id`),
    KEY `idx_da_appraisal_archive_id` (`archive_id`),
    KEY `idx_da_appraisal_result` (`result`),
    KEY `idx_da_appraisal_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='档案鉴定记录表';

-- 16. 销毁记录表
DROP TABLE IF EXISTS `da_destruction`;
CREATE TABLE `da_destruction` (
    `id`              BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '销毁记录ID',
    `destruction_no`  VARCHAR(50)         NOT NULL COMMENT '销毁单号',
    `archive_id`      BIGINT UNSIGNED     NOT NULL COMMENT '销毁档案ID',
    `reason`          VARCHAR(500)        DEFAULT NULL COMMENT '销毁理由',
    `operator_id`     BIGINT UNSIGNED     DEFAULT NULL COMMENT '销毁操作人ID',
    `operator_name`   VARCHAR(50)         DEFAULT NULL COMMENT '销毁操作人姓名',
    `approve_id`      BIGINT UNSIGNED     DEFAULT NULL COMMENT '审批人ID',
    `approve_name`    VARCHAR(50)         DEFAULT NULL COMMENT '审批人姓名',
    `destruction_time` DATETIME           DEFAULT NULL COMMENT '实际销毁时间',
    `create_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '创建者ID',
    `create_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '更新者ID',
    `update_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`        TINYINT UNSIGNED    DEFAULT 0 COMMENT '删除标志: 0正常 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_da_destruction_no` (`destruction_no`),
    KEY `idx_da_destruction_archive_id` (`archive_id`),
    KEY `idx_da_destruction_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='档案销毁记录表';

-- ============================================================
-- 四、库房管理域(可选)
-- ============================================================

-- 17. 库房表
DROP TABLE IF EXISTS `da_warehouse`;
CREATE TABLE `da_warehouse` (
    `id`              BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '库房ID',
    `warehouse_code`  VARCHAR(50)         NOT NULL COMMENT '库房编码',
    `warehouse_name`  VARCHAR(100)        NOT NULL COMMENT '库房名称',
    `location`        VARCHAR(200)        DEFAULT NULL COMMENT '库房位置',
    `status`          TINYINT UNSIGNED    DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    `create_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '创建者ID',
    `create_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '更新者ID',
    `update_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`        TINYINT UNSIGNED    DEFAULT 0 COMMENT '删除标志: 0正常 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_da_warehouse_code` (`warehouse_code`),
    KEY `idx_da_warehouse_status` (`status`),
    KEY `idx_da_warehouse_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='档案库房表';

-- 18. 密集架表
DROP TABLE IF EXISTS `da_shelf`;
CREATE TABLE `da_shelf` (
    `id`              BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '密集架ID',
    `warehouse_id`    BIGINT UNSIGNED     NOT NULL COMMENT '所属库房ID',
    `shelf_code`      VARCHAR(50)         NOT NULL COMMENT '密集架编码',
    `shelf_name`      VARCHAR(100)        NOT NULL COMMENT '密集架名称',
    `status`          TINYINT UNSIGNED    DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    `create_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '创建者ID',
    `create_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '更新者ID',
    `update_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`        TINYINT UNSIGNED    DEFAULT 0 COMMENT '删除标志: 0正常 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_da_shelf_code` (`shelf_code`),
    KEY `idx_da_shelf_warehouse_id` (`warehouse_id`),
    KEY `idx_da_shelf_status` (`status`),
    KEY `idx_da_shelf_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='档案密集架表';

-- 19. 单元格表
DROP TABLE IF EXISTS `da_cell`;
CREATE TABLE `da_cell` (
    `id`              BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '单元格ID',
    `shelf_id`        BIGINT UNSIGNED     NOT NULL COMMENT '所属密集架ID',
    `cell_code`       VARCHAR(50)         NOT NULL COMMENT '单元格编码',
    `cell_name`       VARCHAR(100)        NOT NULL COMMENT '单元格名称',
    `status`          TINYINT UNSIGNED    DEFAULT 1 COMMENT '状态: 0禁用 1启用 2已占用',
    `create_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '创建者ID',
    `create_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       BIGINT UNSIGNED     DEFAULT NULL COMMENT '更新者ID',
    `update_time`     DATETIME            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`        TINYINT UNSIGNED    DEFAULT 0 COMMENT '删除标志: 0正常 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_da_cell_code` (`cell_code`),
    KEY `idx_da_cell_shelf_id` (`shelf_id`),
    KEY `idx_da_cell_status` (`status`),
    KEY `idx_da_cell_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='档案存放单元格表';

-- ============================================================
-- 五、外键约束(可选, 按团队规范决定是否启用)
-- 注意: MyBatis-Plus 逻辑删除与外键同时启用时, 删除前需先处理关联数据
-- ============================================================

ALTER TABLE `sys_user`
    ADD CONSTRAINT `fk_sys_user_role_id` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    ADD CONSTRAINT `fk_sys_user_fonds_id` FOREIGN KEY (`fonds_id`) REFERENCES `da_fonds` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `sys_role_menu`
    ADD CONSTRAINT `fk_sys_role_menu_role_id` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `fk_sys_role_menu_menu_id` FOREIGN KEY (`menu_id`) REFERENCES `sys_menu` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `sys_dict_item`
    ADD CONSTRAINT `fk_sys_dict_item_dict_id` FOREIGN KEY (`dict_id`) REFERENCES `sys_dict` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `da_archive_type`
    ADD CONSTRAINT `fk_da_archive_type_fonds_id` FOREIGN KEY (`fonds_id`) REFERENCES `da_fonds` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `da_volume`
    ADD CONSTRAINT `fk_da_volume_fonds_id` FOREIGN KEY (`fonds_id`) REFERENCES `da_fonds` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `fk_da_volume_type_id` FOREIGN KEY (`type_id`) REFERENCES `da_archive_type` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `da_file`
    ADD CONSTRAINT `fk_da_file_volume_id` FOREIGN KEY (`volume_id`) REFERENCES `da_volume` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    ADD CONSTRAINT `fk_da_file_fonds_id` FOREIGN KEY (`fonds_id`) REFERENCES `da_fonds` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `fk_da_file_type_id` FOREIGN KEY (`type_id`) REFERENCES `da_archive_type` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `fk_da_file_warehouse_id` FOREIGN KEY (`warehouse_id`) REFERENCES `da_warehouse` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    ADD CONSTRAINT `fk_da_file_shelf_id` FOREIGN KEY (`shelf_id`) REFERENCES `da_shelf` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    ADD CONSTRAINT `fk_da_file_cell_id` FOREIGN KEY (`cell_id`) REFERENCES `da_cell` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `da_electronic_file`
    ADD CONSTRAINT `fk_da_electronic_file_archive_id` FOREIGN KEY (`archive_id`) REFERENCES `da_file` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `da_lifecycle`
    ADD CONSTRAINT `fk_da_lifecycle_archive_id` FOREIGN KEY (`archive_id`) REFERENCES `da_file` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `da_borrow`
    ADD CONSTRAINT `fk_da_borrow_archive_id` FOREIGN KEY (`archive_id`) REFERENCES `da_file` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `da_appraisal`
    ADD CONSTRAINT `fk_da_appraisal_archive_id` FOREIGN KEY (`archive_id`) REFERENCES `da_file` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `da_destruction`
    ADD CONSTRAINT `fk_da_destruction_archive_id` FOREIGN KEY (`archive_id`) REFERENCES `da_file` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `da_shelf`
    ADD CONSTRAINT `fk_da_shelf_warehouse_id` FOREIGN KEY (`warehouse_id`) REFERENCES `da_warehouse` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `da_cell`
    ADD CONSTRAINT `fk_da_cell_shelf_id` FOREIGN KEY (`shelf_id`) REFERENCES `da_shelf` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
