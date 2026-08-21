package com.example.documentmanagementsystem.modules.archive.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.documentmanagementsystem.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 档案借阅单实体（D17 申请）
 * 对应表：da_borrow
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("da_borrow")
public class Borrow extends BaseEntity {

    /**
     * 借阅单号（唯一，如 JY-20260820-0001）
     */
    private String borrowNo;

    /**
     * 借阅档案ID（外键 da_file.id）
     */
    private Long archiveId;

    /**
     * 申请人ID
     */
    private Long applicantId;

    /**
     * 申请人姓名
     */
    private String applicantName;

    /**
     * 借阅理由
     */
    private String reason;

    /**
     * 申请时间
     */
    private LocalDateTime applyTime;

    /**
     * 状态：1-待审批 2-已通过 3-已驳回 4-已归还
     */
    private Integer status;

    /**
     * 审批人ID
     */
    private Long approverId;

    /**
     * 审批人姓名
     */
    private String approverName;

    /**
     * 审批时间
     */
    private LocalDateTime approveTime;

    /**
     * 审批意见
     */
    private String approveComment;

    /**
     * 归还时间
     */
    private LocalDateTime returnTime;
}
