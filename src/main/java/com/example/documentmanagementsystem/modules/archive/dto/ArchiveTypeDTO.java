package com.example.documentmanagementsystem.modules.archive.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 门类新增/编辑参数
 */
@Data
@ApiModel("门类参数")
public class ArchiveTypeDTO {

    @ApiModelProperty("门类ID（编辑时必填，新增时为空）")
    private Long id;

    @NotBlank(message = "门类代码不能为空")
    @ApiModelProperty(value = "门类代码（同一全宗内唯一，如 WS）", required = true, example = "WS")
    private String typeCode;

    @NotBlank(message = "门类名称不能为空")
    @ApiModelProperty(value = "门类名称（如 文书档案）", required = true, example = "文书档案")
    private String typeName;

    @NotNull(message = "所属全宗不能为空")
    @ApiModelProperty(value = "所属全宗ID", required = true, example = "1")
    private Long fondsId;

    @ApiModelProperty("门类描述")
    private String description;

    @ApiModelProperty(value = "默认保管期限（字典值）", example = "permanent")
    private String retentionPeriod;

    @ApiModelProperty(value = "显示排序", example = "0")
    private Integer sort;

    @ApiModelProperty(value = "状态：0-禁用 1-启用", example = "1")
    private Integer status;
}
