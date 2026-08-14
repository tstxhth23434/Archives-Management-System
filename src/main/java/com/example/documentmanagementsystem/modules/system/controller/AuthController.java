package com.example.documentmanagementsystem.modules.system.controller;

import com.example.documentmanagementsystem.common.base.BaseController;
import com.example.documentmanagementsystem.common.result.Result;
import com.example.documentmanagementsystem.modules.system.dto.ChangePasswordDTO;
import com.example.documentmanagementsystem.modules.system.dto.LoginDTO;
import com.example.documentmanagementsystem.modules.system.entity.SysMenu;
import com.example.documentmanagementsystem.modules.system.service.ISysMenuService;
import com.example.documentmanagementsystem.modules.system.service.ISysUserService;
import com.example.documentmanagementsystem.modules.system.vo.LoginVO;
import com.example.documentmanagementsystem.modules.system.vo.UserInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 登录认证接口
 * 路径 /api/auth/** 已在 SaTokenConfig 白名单中放行
 */
@Slf4j
@Api(tags = "01-登录认证")
@RestController
@RequestMapping("/api/auth")
public class AuthController extends BaseController {

    @Resource
    private ISysUserService sysUserService;

    @Resource
    private ISysMenuService sysMenuService;

    /**
     * 用户登录
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Validated @RequestBody LoginDTO dto, HttpServletRequest request) {
        // 获取客户端 IP（兼容反向代理 X-Forwarded-For）
        String ip = getClientIp(request);
        return success("登录成功", sysUserService.login(dto, ip));
    }

    /**
     * 退出登录
     */
    @ApiOperation("退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        sysUserService.logout();
        return success("退出成功", null);
    }

    /**
     * 获取当前登录用户信息
     */
    @ApiOperation("获取当前登录用户信息")
    @GetMapping("/info")
    public Result<UserInfoVO> info() {
        return success(sysUserService.getCurrentUserInfo());
    }

    /**
     * 修改密码（修改成功后强制下线，需用新密码重新登录）
     */
    @ApiOperation("修改密码")
    @PostMapping("/change-password")
    public Result<Void> changePassword(@Validated @RequestBody ChangePasswordDTO dto) {
        sysUserService.changePassword(dto);
        return success("修改成功，请重新登录", null);
    }

    /**
     * 获取当前登录用户的菜单树 + 权限码
     * 前端根据菜单树动态渲染侧边栏，根据权限码控制按钮显隐
     */
    @ApiOperation("获取当前用户菜单树和权限码")
    @GetMapping("/menus")
    public Result<Map<String, Object>> menus() {
        Long userId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
        List<SysMenu> menuTree = sysMenuService.listUserMenuTree(userId);
        List<String> perms = sysMenuService.listUserPerms(userId);
        Map<String, Object> data = new java.util.HashMap<>(4);
        data.put("menus", menuTree);
        data.put("perms", perms);
        return success(data);
    }

    /**
     * 获取客户端真实 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // X-Forwarded-For 可能包含多个代理 IP，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
