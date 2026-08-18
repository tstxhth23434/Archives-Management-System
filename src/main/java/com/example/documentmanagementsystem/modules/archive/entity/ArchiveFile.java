package com.example.documentmanagementsystem.modules.archive.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.documentmanagementsystem.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 档案文件实体（D10 著录）
 * 对应表：da_file
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("da_file")
public class ArchiveFile extends BaseEntity {

    /**
     * 所属案卷ID（未组卷可为空）
     */
    private Long volumeId;

    /**
     * 档号（唯一，如 JSXY-WS-2026-0001，自动生成）
     */
    private String archiveNo;

    /**
     * 题名
     */
    private String title;

    /**
     * 责任者
     */
    private String author;

    /**
     * 文件日期
     */
    private LocalDate docDate;

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
     * 关键词（逗号分隔）
     */
    private String keywords;

    /**
     * 页数
     */
    private Integer pages;

    /**
     * 摘要/备注
     */
    private String summary;

    /**
     * 状态：1-整理中 2-已归档 3-已封库 4-已销毁
     */
    private Integer status;

    /**
     * 所属全宗ID
     */
    private Long fondsId;

    /**
     * 所属门类ID
     */
    private Long typeId;

    /**
     * 存放库房ID（可选）
     */
    private Long warehouseId;

    /**
     * 存放密集架ID（可选）
     */
    private Long shelfId;

    /**
     * 存放单元格ID（可选）
     */
    private Long cellId;
}
