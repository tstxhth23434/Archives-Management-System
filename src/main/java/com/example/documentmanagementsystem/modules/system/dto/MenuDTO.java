package com.example.documentmanagementsystem.modules.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 菜单新增/编辑参数
 */
@Data
@ApiModel("菜单参数")
public class MenuDTO {

    @ApiModelProperty("菜单ID（编辑时必填，新增时为空）")
    private Long id;

    @ApiModelProperty(value = "父菜单ID（0 为顶级）", example = "0")
    private Long parentId;

    @NotBlank(message = "菜单名称不能为空")
    @ApiModelProperty(value = "菜单名称", required = true, example = "用户管理")
    private String menuName;

    @ApiModelProperty("菜单标题（前端显示）")
    private String menuTitle;

    @NotNull(message = "菜单类型不能为空")
    @ApiModelProperty(value = "菜单类型：1-目录 2-菜单 3-按钮", required = true, example = "2")
    private Integer menuType;

    @ApiModelProperty("菜单图标")
    private String icon;

    @ApiModelProperty("路由路径")
    private String path;

    @ApiModelProperty("组件路径")
    private String component;

    @ApiModelProperty("权限标识，如 archive:file:list")
    private String perms;

    @ApiModelProperty(value = "显示排序", example = "0")
    private Integer sort;

    @ApiModelProperty(value = "状态：0-禁用 1-启用", example = "1")
    private Integer status;
}
