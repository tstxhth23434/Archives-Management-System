package com.example.documentmanagementsystem.modules.archive.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.documentmanagementsystem.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 档案案卷实体（D9 仅用于只读查询，CRUD 与档号生成在 D10）
 * 对应表：da_volume
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("da_volume")
public class ArchiveVolume extends BaseEntity {

    /**
     * 所属全宗ID
     */
    private Long fondsId;

    /**
     * 所属门类ID
     */
    private Long typeId;

    /**
     * 案卷号（唯一，D10 档号规则生成）
     */
    private String volumeNo;

    /**
     * 案卷题名
     */
    private String title;

    /**
     * 年度
     */
    private Integer year;

    /**
     * 保管期限（字典值）
     */
    private String retentionPeriod;

    /**
     * 密级（字典值）
     */
    private String securityLevel;

    /**
     * 状态：1-整理中 2-已归档 3-已封库
     */
    private Integer status;
}
