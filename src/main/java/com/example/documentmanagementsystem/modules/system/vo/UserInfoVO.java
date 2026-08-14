package com.example.documentmanagementsystem.modules.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 当前登录用户信息（脱敏，不含密码）
 */
@Data
@ApiModel("用户信息")
public class UserInfoVO {

    @ApiModelProperty("用户ID")
    private Long id;

    @ApiModelProperty("登录账号")
    private String username;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("角色ID")
    private Long roleId;

    @ApiModelProperty("所属全宗ID")
    private Long fondsId;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("头像地址")
    private String avatar;

    @ApiModelProperty("最后登录时间")
    private LocalDateTime loginTime;
}
