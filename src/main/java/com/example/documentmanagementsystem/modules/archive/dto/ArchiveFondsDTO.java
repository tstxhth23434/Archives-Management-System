package com.example.documentmanagementsystem.modules.archive.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 全宗新增/编辑参数
 */
@Data
@ApiModel("全宗参数")
public class ArchiveFondsDTO {

    @ApiModelProperty("全宗ID（编辑时必填，新增时为空）")
    private Long id;

    @NotBlank(message = "全宗号不能为空")
    @ApiModelProperty(value = "全宗号（唯一，如 JSXY）", required = true, example = "JSXY")
    private String fondsCode;

    @NotBlank(message = "全宗名称不能为空")
    @ApiModelProperty(value = "全宗名称（如 计算机学院）", required = true, example = "计算机学院")
    private String fondsName;

    @ApiModelProperty("全宗描述")
    private String description;

    @ApiModelProperty(value = "状态：0-禁用 1-启用", example = "1")
    private Integer status;

    @ApiModelProperty(value = "显示排序", example = "0")
    private Integer sort;
}
