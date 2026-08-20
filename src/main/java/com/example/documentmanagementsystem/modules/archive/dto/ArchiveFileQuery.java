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

    @ApiModelProperty("档号（模糊，D16 检索）")
    private String archiveNo;

    @ApiModelProperty("关键词（模糊，D16 检索）")
    private String keywords;

    @ApiModelProperty("保管期限（字典值，D16 检索）")
    private String retentionPeriod;

    @ApiModelProperty("密级（字典值，D16 检索）")
    private String securityLevel;
}
