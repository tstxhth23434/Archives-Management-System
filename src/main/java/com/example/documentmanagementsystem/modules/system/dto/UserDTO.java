package com.example.documentmanagementsystem.modules.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 用户新增/编辑参数
 * 说明：新增时 password 必填；编辑时 password 为空表示不修改密码
 */
@Data
@ApiModel("用户参数")
public class UserDTO {

    @ApiModelProperty("用户ID（编辑时必填，新增时为空）")
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty(value = "登录账号（唯一）", required = true, example = "zhangsan")
    private String username;

    @ApiModelProperty(value = "密码（新增必填；编辑为空表示不修改）", example = "abc12345")
    private String password;

    @ApiModelProperty("真实姓名")
    private String realName;

    @NotNull(message = "角色不能为空")
    @ApiModelProperty(value = "角色ID", required = true, example = "1")
    private Long roleId;

    @ApiModelProperty("所属全宗ID")
    private Long fondsId;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty(value = "状态：0-禁用 1-启用", example = "1")
    private Integer status;
}
