package com.example.documentmanagementsystem.modules.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 角色新增/编辑参数
 */
@Data
@ApiModel("角色参数")
public class RoleDTO {

    @ApiModelProperty("角色ID（编辑时必填，新增时为空）")
    private Long id;

    @NotBlank(message = "角色名称不能为空")
    @ApiModelProperty(value = "角色名称", required = true, example = "档案管理员")
    private String roleName;

    @NotBlank(message = "角色编码不能为空")
    @ApiModelProperty(value = "角色编码（唯一，如 archive_admin）", required = true, example = "archive_admin")
    private String roleCode;

    @ApiModelProperty("角色描述")
    private String description;

    @ApiModelProperty(value = "状态：0-禁用 1-启用", example = "1")
    private Integer status;

    @ApiModelProperty(value = "显示排序", example = "0")
    private Integer sort;
}
