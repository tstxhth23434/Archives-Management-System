package com.example.documentmanagementsystem.modules.system.dto;

import com.example.documentmanagementsystem.common.base.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户分页查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("用户分页查询参数")
public class UserQuery extends PageQuery {

    @ApiModelProperty("登录账号（模糊查询）")
    private String username;

    @ApiModelProperty("真实姓名（模糊查询）")
    private String realName;

    @ApiModelProperty("角色ID")
    private Long roleId;

    @ApiModelProperty("状态：0-禁用 1-启用")
    private Integer status;
}
