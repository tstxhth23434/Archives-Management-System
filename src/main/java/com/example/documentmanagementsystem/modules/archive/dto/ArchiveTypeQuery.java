package com.example.documentmanagementsystem.modules.archive.dto;

import com.example.documentmanagementsystem.common.base.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 门类分页查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("门类分页查询参数")
public class ArchiveTypeQuery extends PageQuery {

    @ApiModelProperty("门类名称（模糊查询）")
    private String typeName;

    @ApiModelProperty("门类代码（模糊查询）")
    private String typeCode;

    @ApiModelProperty("所属全宗ID")
    private Long fondsId;

    @ApiModelProperty("状态：0-禁用 1-启用")
    private Integer status;
}
