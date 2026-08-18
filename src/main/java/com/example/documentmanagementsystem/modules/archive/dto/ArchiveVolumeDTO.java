package com.example.documentmanagementsystem.modules.archive.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 案卷新增/编辑参数（D10，档号由后端按规则自动生成）
 */
@Data
@ApiModel("案卷参数")
public class ArchiveVolumeDTO {

    @ApiModelProperty("案卷ID（编辑时必填，新增时为空）")
    private Long id;

    @NotNull(message = "所属全宗不能为空")
    @ApiModelProperty(value = "所属全宗ID", required = true, example = "1")
    private Long fondsId;

    @NotNull(message = "所属门类不能为空")
    @ApiModelProperty(value = "所属门类ID", required = true, example = "1")
    private Long typeId;

    @NotBlank(message = "案卷题名不能为空")
    @ApiModelProperty(value = "案卷题名", required = true, example = "计算机学院2023年行政文件卷")
    private String title;

    @NotNull(message = "年度不能为空（档号规则需要）")
    @ApiModelProperty(value = "年度", required = true, example = "2023")
    private Integer year;

    @ApiModelProperty(value = "保管期限（字典值）", example = "permanent")
    private String retentionPeriod;

    @ApiModelProperty(value = "密级（字典值）", example = "open")
    private String securityLevel;

    @ApiModelProperty(value = "状态：1-整理中 2-已归档 3-已封库", example = "1")
    private Integer status;
}
