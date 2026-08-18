package com.example.documentmanagementsystem.modules.archive.dto;

import com.example.documentmanagementsystem.common.base.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 全宗分页查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("全宗分页查询参数")
public class ArchiveFondsQuery extends PageQuery {

    @ApiModelProperty("全宗名称（模糊查询）")
    private String fondsName;

    @ApiModelProperty("全宗号（模糊查询）")
    private String fondsCode;

    @ApiModelProperty("状态：0-禁用 1-启用")
    private Integer status;
}
