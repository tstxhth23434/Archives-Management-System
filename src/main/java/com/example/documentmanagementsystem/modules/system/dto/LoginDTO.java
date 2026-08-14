package com.example.documentmanagementsystem.modules.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求参数
 */
@Data
@ApiModel("登录请求参数")
public class LoginDTO {

    /**
     * 登录账号
     */
    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty(value = "登录账号", required = true, example = "admin")
    private String username;

    /**
     * 登录密码
     */
    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(value = "登录密码", required = true, example = "admin123")
    private String password;
}
