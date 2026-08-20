package com.example.documentmanagementsystem.modules.archive.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 档案生命周期履历实体（D15 状态流转记录）
 * 对应表：da_lifecycle（无公共字段，独立实体）
 */
@Data
@TableName("da_lifecycle")
public class Lifecycle implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属档案ID（外键 da_file.id）
     */
    private Long archiveId;

    /**
     * 动作编码（ARCHIVE-归档 / SEAL-封库 等）
     */
    private String action;

    /**
     * 动作名称（如 归档、封库）
     */
    private String actionName;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 操作详情
     */
    private String detail;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
