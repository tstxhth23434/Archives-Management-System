package com.example.documentmanagementsystem.modules.system.dto;

import com.example.documentmanagementsystem.common.constant.PasswordConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 修改密码请求参数
 */
@Data
@ApiModel("修改密码请求参数")
public class ChangePasswordDTO {

    /**
     * 旧密码（不校验格式：历史密码可能不符合新规则，只做非空校验）
     */
    @NotBlank(message = "旧密码不能为空")
    @ApiModelProperty(value = "旧密码", required = true, example = "admin123")
    private String oldPassword;

    /**
     * 新密码（8-16 位，字母+数字组合，正则校验）
     */
    @NotBlank(message = "新密码不能为空")
    @Pattern(regexp = PasswordConstants.PASSWORD_REGEX, message = PasswordConstants.PASSWORD_MESSAGE)
    @ApiModelProperty(value = "新密码", required = true, example = "abc12345")
    private String newPassword;
}
