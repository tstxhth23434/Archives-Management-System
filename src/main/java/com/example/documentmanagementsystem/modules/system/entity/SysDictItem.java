package com.example.documentmanagementsystem.modules.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.documentmanagementsystem.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统字典项实体
 * 对应表：sys_dict_item
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_item")
public class SysDictItem extends BaseEntity {

    /**
     * 字典类型ID
     */
    private Long dictId;

    /**
     * 字典项编码
     */
    private String itemCode;

    /**
     * 字典项名称
     */
    private String itemName;

    /**
     * 字典项值
     */
    private String itemValue;

    /**
     * 显示排序
     */
    private Integer sort;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
}
