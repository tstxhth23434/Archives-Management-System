package com.example.documentmanagementsystem.modules.archive.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel 批量导入结果（D13）
 */
@Data
@ApiModel("批量导入结果")
public class ImportResultVO {

    @ApiModelProperty("总行数（不含表头）")
    private int total;

    @ApiModelProperty("成功条数")
    private int success;

    @ApiModelProperty("失败条数")
    private int fail;

    @ApiModelProperty("错误明细（行号从 2 开始）")
    private List<ErrorItem> errors = new ArrayList<>();

    @Data
    @ApiModel("导入错误明细")
    public static class ErrorItem {

        @ApiModelProperty("Excel 行号（表头为第 1 行）")
        private int row;

        @ApiModelProperty("失败原因")
        private String message;

        public ErrorItem() {
        }

        public ErrorItem(int row, String message) {
            this.row = row;
            this.message = message;
        }
    }
}
