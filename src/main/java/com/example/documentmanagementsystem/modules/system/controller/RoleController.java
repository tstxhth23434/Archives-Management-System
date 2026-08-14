package com.example.documentmanagementsystem.modules.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.documentmanagementsystem.common.annotation.OpLog;
import com.example.documentmanagementsystem.common.base.BaseController;
import com.example.documentmanagementsystem.common.result.Result;
import com.example.documentmanagementsystem.modules.system.dto.RoleDTO;
import com.example.documentmanagementsystem.modules.system.dto.RoleQuery;
import com.example.documentmanagementsystem.modules.system.entity.SysRole;
import com.example.documentmanagementsystem.modules.system.service.ISysMenuService;
import com.example.documentmanagementsystem.modules.system.service.ISysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 角色管理接口
 * 路径 /api/system/** 需登录访问（Sa-Token 拦截）
 * 增删改通过 @OpLog 记录操作日志（D6）
 */
@Api(tags = "02-角色管理")
@RestController
@RequestMapping("/api/system/role")
public class RoleController extends BaseController {

    @Resource
    private ISysRoleService roleService;

    @Resource
    private ISysMenuService menuService;

    @ApiOperation("分页查询角色")
    @GetMapping("/page")
    public Result<IPage<SysRole>> page(RoleQuery query) {
        return success(roleService.pageRoles(query));
    }

    @ApiOperation("查询全部启用角色（下拉框用）")
    @GetMapping("/list")
    public Result<List<SysRole>> list() {
        return success(roleService.listEnabledRoles());
    }

    @ApiOperation("新增角色")
    @OpLog("新增角色")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody RoleDTO dto) {
        roleService.createRole(dto);
        return success("新增成功", null);
    }

    @ApiOperation("编辑角色")
    @OpLog("编辑角色")
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody RoleDTO dto) {
        roleService.updateRole(dto);
        return success("修改成功", null);
    }

    @ApiOperation("删除角色")
    @OpLog("删除角色")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return success("删除成功", null);
    }

    @ApiOperation("查询角色已分配的菜单ID列表")
    @GetMapping("/{id}/menus")
    public Result<List<Long>> listMenus(@PathVariable Long id) {
        return success(menuService.listMenuIdsByRole(id));
    }

    @ApiOperation("给角色分配菜单")
    @OpLog("角色分配菜单")
    @PostMapping("/{id}/menus")
    public Result<Void> assignMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        menuService.assignMenusToRole(id, menuIds);
        return success("分配成功", null);
    }
}
