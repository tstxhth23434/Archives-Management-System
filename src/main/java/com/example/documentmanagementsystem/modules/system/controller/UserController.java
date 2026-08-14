package com.example.documentmanagementsystem.modules.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.documentmanagementsystem.common.base.BaseController;
import com.example.documentmanagementsystem.common.result.Result;
import com.example.documentmanagementsystem.modules.system.dto.UserDTO;
import com.example.documentmanagementsystem.modules.system.dto.UserQuery;
import com.example.documentmanagementsystem.modules.system.entity.SysUser;
import com.example.documentmanagementsystem.modules.system.service.ISysUserService;
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

/**
 * 用户管理接口
 * 路径 /api/system/** 需登录访问（Sa-Token 拦截）
 */
@Api(tags = "03-用户管理")
@RestController
@RequestMapping("/api/system/user")
public class UserController extends BaseController {

    @Resource
    private ISysUserService sysUserService;

    @ApiOperation("分页查询用户")
    @GetMapping("/page")
    public Result<IPage<SysUser>> page(UserQuery query) {
        return success(sysUserService.pageUsers(query));
    }

    @ApiOperation("新增用户")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody UserDTO dto) {
        sysUserService.createUser(dto);
        return success("新增成功", null);
    }

    @ApiOperation("编辑用户")
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody UserDTO dto) {
        sysUserService.updateUser(dto);
        return success("修改成功", null);
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserService.deleteUser(id);
        return success("删除成功", null);
    }

    @ApiOperation("启用/禁用用户")
    @PutMapping("/{id}/status/{status}")
    public Result<Void> changeStatus(@PathVariable Long id, @PathVariable Integer status) {
        sysUserService.changeUserStatus(id, status);
        return success(status != null && status == 1 ? "启用成功" : "禁用成功", null);
    }

    @ApiOperation("重置用户密码为默认密码")
    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id) {
        sysUserService.resetPassword(id);
        return success("重置成功，默认密码为 admin123", null);
    }
}
