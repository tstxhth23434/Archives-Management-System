package com.example.documentmanagementsystem.modules.system.controller;

import com.example.documentmanagementsystem.common.annotation.OpLog;
import com.example.documentmanagementsystem.common.base.BaseController;
import com.example.documentmanagementsystem.common.result.Result;
import com.example.documentmanagementsystem.modules.system.dto.MenuDTO;
import com.example.documentmanagementsystem.modules.system.entity.SysMenu;
import com.example.documentmanagementsystem.modules.system.service.ISysMenuService;
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
 * 菜单管理接口
 * 路径 /api/system/** 需登录访问（Sa-Token 拦截）
 * 增删改通过 @OpLog 记录操作日志（D6）
 */
@Api(tags = "04-菜单管理")
@RestController
@RequestMapping("/api/system/menu")
public class MenuController extends BaseController {

    @Resource
    private ISysMenuService menuService;

    @ApiOperation("查询菜单树（管理端）")
    @GetMapping("/tree")
    public Result<List<SysMenu>> tree() {
        return success(menuService.listMenuTree());
    }

    @ApiOperation("新增菜单")
    @OpLog("新增菜单")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody MenuDTO dto) {
        menuService.createMenu(dto);
        return success("新增成功", null);
    }

    @ApiOperation("编辑菜单")
    @OpLog("编辑菜单")
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody MenuDTO dto) {
        menuService.updateMenu(dto);
        return success("修改成功", null);
    }

    @ApiOperation("删除菜单")
    @OpLog("删除菜单")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return success("删除成功", null);
    }
}
