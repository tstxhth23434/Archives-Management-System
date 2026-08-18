package com.example.documentmanagementsystem.modules.archive.dto;

import com.example.documentmanagementsystem.common.base.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 档案文件分页查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("档案文件分页查询参数")
public class ArchiveFileQuery extends PageQuery {

    @ApiModelProperty("所属全宗ID")
    private Long fondsId;

    @ApiModelProperty("所属门类ID")
    private Long typeId;

    @ApiModelProperty("所属案卷ID")
    private Long volumeId;

    @ApiModelProperty("年度")
    private Integer year;

    @ApiModelProperty("题名（模糊）")
    private String title;
}
