package com.example.documentmanagementsystem.common.config;

import cn.dev33.satoken.stp.StpInterface;
import com.example.documentmanagementsystem.modules.system.entity.SysRole;
import com.example.documentmanagementsystem.modules.system.entity.SysRoleMenu;
import com.example.documentmanagementsystem.modules.system.entity.SysMenu;
import com.example.documentmanagementsystem.modules.system.entity.SysUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.documentmanagementsystem.modules.system.mapper.SysMenuMapper;
import com.example.documentmanagementsystem.modules.system.mapper.SysRoleMapper;
import com.example.documentmanagementsystem.modules.system.mapper.SysRoleMenuMapper;
import com.example.documentmanagementsystem.modules.system.mapper.SysUserMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Sa-Token 权限数据源
 * 提供当前登录用户的权限码列表与角色列表，供 @SaCheckPermission / @SaCheckRole 注解鉴权使用
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Resource
    private SysMenuMapper sysMenuMapper;

    /**
     * 返回当前账号拥有的权限码列表（如 archive:file:list）
     * loginId 由 Sa-Token 传入，即登录时 StpUtil.login(userId) 的 userId
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long userId = Long.valueOf(loginId.toString());
        // 1. 查用户角色
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || user.getRoleId() == null) {
            return Collections.emptyList();
        }
        // 2. 查角色关联的菜单
        List<Long> menuIds = sysRoleMenuMapper.selectList(new LambdaQueryWrapper<SysRoleMenu>()
                        .eq(SysRoleMenu::getRoleId, user.getRoleId()))
                .stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toList());
        if (menuIds.isEmpty()) {
            return Collections.emptyList();
        }
        // 3. 收集菜单上的权限标识（非空且去重）
        return sysMenuMapper.selectBatchIds(menuIds).stream()
                .map(SysMenu::getPerms)
                .filter(perms -> perms != null && !perms.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 返回当前账号拥有的角色编码列表（如 super_admin）
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long userId = Long.valueOf(loginId.toString());
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || user.getRoleId() == null) {
            return Collections.emptyList();
        }
        SysRole role = sysRoleMapper.selectById(user.getRoleId());
        return role == null ? Collections.emptyList() : Collections.singletonList(role.getRoleCode());
    }
}
