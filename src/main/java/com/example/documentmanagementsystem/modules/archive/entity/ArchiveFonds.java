package com.example.documentmanagementsystem.modules.archive.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.documentmanagementsystem.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 档案全宗实体
 * 对应表：da_fonds（一个全宗 = 一个立档单位，如"计算机学院"）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("da_fonds")
public class ArchiveFonds extends BaseEntity {

    /**
     * 全宗号（唯一，如 JSXY）
     */
    private String fondsCode;

    /**
     * 全宗名称（如 计算机学院）
     */
    private String fondsName;

    /**
     * 全宗描述
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
