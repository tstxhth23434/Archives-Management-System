package com.example.documentmanagementsystem.modules.archive.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.documentmanagementsystem.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 档案门类实体
 * 对应表：da_archive_type（文书/科技/会计/声像等，隶属于某个全宗）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("da_archive_type")
public class ArchiveType extends BaseEntity {

    /**
     * 门类代码（同一全宗内唯一，如 WS）
     */
    private String typeCode;

    /**
     * 门类名称（如 文书档案）
     */
    private String typeName;

    /**
     * 所属全宗ID（外键 da_fonds.id）
     */
    private Long fondsId;

    /**
     * 门类描述
     */
    private String description;

    /**
     * 默认保管期限（字典值，如 permanent/long_term/short_term）
     */
    private String retentionPeriod;

    /**
     * 显示排序
     */
    private Integer sort;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
}
