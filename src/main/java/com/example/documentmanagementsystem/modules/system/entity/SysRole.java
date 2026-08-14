package com.example.documentmanagementsystem.modules.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.documentmanagementsystem.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统角色实体
 * 对应表：sys_role
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码（唯一，如 super_admin）
     */
    private String roleCode;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 显示排序
     */
    private Integer sort;
}
