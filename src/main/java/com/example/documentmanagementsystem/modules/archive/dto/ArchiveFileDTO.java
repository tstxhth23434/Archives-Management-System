package com.example.documentmanagementsystem.modules.archive.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 档案文件著录参数（D10，档号由后端按规则自动生成）
 */
@Data
@ApiModel("档案文件著录参数")
public class ArchiveFileDTO {

    @ApiModelProperty("文件ID（编辑时必填，新增时为空）")
    private Long id;

    @ApiModelProperty("所属案卷ID（未组卷可为空）")
    private Long volumeId;

    @NotBlank(message = "题名不能为空")
    @ApiModelProperty(value = "题名", required = true, example = "关于2023年教学改革的通知")
    private String title;

    @ApiModelProperty(value = "责任者", example = "教务处")
    private String author;

    @ApiModelProperty("文件日期")
    private LocalDate docDate;

    @NotNull(message = "年度不能为空（档号规则需要）")
    @ApiModelProperty(value = "年度", required = true, example = "2023")
    private Integer year;

    @ApiModelProperty(value = "保管期限（字典值）", example = "permanent")
    private String retentionPeriod;

    @ApiModelProperty(value = "密级（字典值）", example = "open")
    private String securityLevel;

    @ApiModelProperty(value = "关键词（逗号分隔）", example = "教学改革,通知")
    private String keywords;

    @ApiModelProperty(value = "页数", example = "5")
    private Integer pages;

    @ApiModelProperty("摘要/备注")
    private String summary;

    @ApiModelProperty(value = "状态：1-整理中 2-已归档 3-已封库", example = "1")
    private Integer status;

    @NotNull(message = "所属全宗不能为空")
    @ApiModelProperty(value = "所属全宗ID", required = true, example = "1")
    private Long fondsId;

    @NotNull(message = "所属门类不能为空")
    @ApiModelProperty(value = "所属门类ID", required = true, example = "1")
    private Long typeId;
}
