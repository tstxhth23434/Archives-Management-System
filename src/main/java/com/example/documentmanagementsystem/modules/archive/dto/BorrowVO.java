package com.example.documentmanagementsystem.modules.archive.dto;

import com.example.documentmanagementsystem.modules.archive.entity.Borrow;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 借阅单视图（含档案题名，列表展示）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("借阅单视图")
public class BorrowVO extends Borrow {

    @ApiModelProperty("档案题名")
    private String archiveTitle;

    @ApiModelProperty("档案档号")
    private String archiveNo;
}
