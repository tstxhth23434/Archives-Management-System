package com.example.documentmanagementsystem.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.documentmanagementsystem.modules.system.dto.MenuDTO;
import com.example.documentmanagementsystem.modules.system.entity.SysMenu;

import java.util.List;

/**
 * 系统菜单服务
 */
public interface ISysMenuService extends IService<SysMenu> {

    /**
     * 查询全部菜单树（管理端用）
     */
    List<SysMenu> listMenuTree();

    /**
     * 新增菜单
     */
    void createMenu(MenuDTO dto);

    /**
     * 编辑菜单
     */
    void updateMenu(MenuDTO dto);

    /**
     * 删除菜单（有子菜单时禁止删除）
     */
    void deleteMenu(Long id);

    /**
     * 查询指定角色已分配的菜单ID列表
     */
    List<Long> listMenuIdsByRole(Long roleId);

    /**
     * 给角色分配菜单（先删后插，事务）
     */
    void assignMenusToRole(Long roleId, List<Long> menuIds);

    /**
     * 查询当前登录用户的菜单树（按角色关联菜单过滤）
     */
    List<SysMenu> listUserMenuTree(Long userId);

    /**
     * 查询当前登录用户的权限码列表（如 archive:file:list）
     */
    List<String> listUserPerms(Long userId);
}
