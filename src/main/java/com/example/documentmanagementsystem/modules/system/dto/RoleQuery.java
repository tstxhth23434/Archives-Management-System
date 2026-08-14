package com.example.documentmanagementsystem.modules.system.dto;

import com.example.documentmanagementsystem.common.base.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色分页查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("角色分页查询参数")
public class RoleQuery extends PageQuery {

    @ApiModelProperty("角色名称（模糊查询）")
    private String roleName;

    @ApiModelProperty("状态：0-禁用 1-启用")
    private Integer status;
}
