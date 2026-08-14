package com.example.documentmanagementsystem.modules.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.documentmanagementsystem.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统字典类型实体
 * 对应表：sys_dict（如保管期限、密级、档案状态等枚举）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict")
public class SysDict extends BaseEntity {

    /**
     * 字典编码（唯一，如 retention_period）
     */
    private String dictCode;

    /**
     * 字典名称（如 保管期限）
     */
    private String dictName;

    /**
     * 字典描述
     */
    private String description;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
}
