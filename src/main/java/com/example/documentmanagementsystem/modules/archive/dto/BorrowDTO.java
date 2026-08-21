package com.example.documentmanagementsystem.modules.archive.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 借阅申请参数（D17）
 */
@Data
@ApiModel("借阅申请参数")
public class BorrowDTO {

    @NotNull(message = "借阅档案不能为空")
    @ApiModelProperty(value = "档案ID", required = true, example = "11")
    private Long archiveId;

    @NotBlank(message = "借阅理由不能为空")
    @ApiModelProperty(value = "借阅理由", required = true, example = "用于论文写作参考")
    private String reason;
}
