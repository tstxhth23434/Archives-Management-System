package com.example.documentmanagementsystem.modules.archive.dto;

import com.example.documentmanagementsystem.common.base.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 案卷分页查询参数（D9 只读浏览）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("案卷分页查询参数")
public class ArchiveVolumeQuery extends PageQuery {

    @ApiModelProperty("所属全宗ID")
    private Long fondsId;

    @ApiModelProperty("所属门类ID")
    private Long typeId;

    @ApiModelProperty("年度")
    private Integer year;

    @ApiModelProperty("案卷题名（模糊）")
    private String title;
}
