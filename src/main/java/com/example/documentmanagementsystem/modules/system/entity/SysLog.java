package com.example.documentmanagementsystem.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统操作日志实体
 * 对应表：sys_log（注意：该表无 del_flag 等公共字段，不继承 BaseEntity）
 */
@Data
@TableName("sys_log")
public class SysLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID，数据库自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 操作用户ID
     */
    private Long userId;

    /**
     * 操作用户账号
     */
    private String username;

    /**
     * 操作描述
     */
    private String operation;

    /**
     * 请求方法（类名.方法名）
     */
    private String method;

    /**
     * 请求参数（JSON，密码已脱敏）
     */
    private String params;

    /**
     * 操作IP
     */
    private String ip;

    /**
     * 耗时（毫秒）
     */
    private Integer spendTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
