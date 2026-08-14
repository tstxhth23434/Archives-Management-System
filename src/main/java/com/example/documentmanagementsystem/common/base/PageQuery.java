package com.example.documentmanagementsystem.common.base;

import lombok.Data;

/**
 * 分页查询参数基类
 * 所有带分页的查询请求 DTO 都继承此类
 */
@Data
public class PageQuery {

    /**
     * 当前页码（从 1 开始）
     */
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 10;

    /**
     * 排序字段（对应数据库列名，如 create_time）
     */
    private String orderByColumn;

    /**
     * 排序方向：asc / desc
     */
    private String isAsc = "desc";
}
