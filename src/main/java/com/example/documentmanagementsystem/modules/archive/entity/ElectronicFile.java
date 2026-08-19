package com.example.documentmanagementsystem.modules.archive.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.documentmanagementsystem.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 档案电子原文实体（D12 上传）
 * 对应表：da_electronic_file
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("da_electronic_file")
public class ElectronicFile extends BaseEntity {

    /**
     * 所属档案ID（外键 da_file.id）
     */
    private Long archiveId;

    /**
     * 原始文件名
     */
    private String fileName;

    /**
     * 存储路径（相对上传根目录）
     */
    private String filePath;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件 MIME 类型
     */
    private String fileType;

    /**
     * 文件后缀（如 pdf）
     */
    private String fileSuffix;

    /**
     * 是否原件：0-否 1-是
     */
    private Integer isOriginal;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 上传时间
     */
    private java.time.LocalDateTime uploadTime;
}
