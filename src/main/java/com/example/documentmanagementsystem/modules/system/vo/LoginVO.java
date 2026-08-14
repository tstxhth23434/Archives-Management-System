package com.example.documentmanagementsystem.modules.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 登录成功返回结果
 */
@Data
@ApiModel("登录结果")
public class LoginVO {

    @ApiModelProperty("访问令牌（后续请求放入 Authorization 请求头）")
    private String token;

    @ApiModelProperty("用户信息")
    private UserInfoVO userInfo;
}
