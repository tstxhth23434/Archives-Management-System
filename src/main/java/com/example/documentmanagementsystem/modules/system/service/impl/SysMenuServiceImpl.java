package com.example.documentmanagementsystem.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.documentmanagementsystem.common.exception.ServiceException;
import com.example.documentmanagementsystem.modules.system.dto.MenuDTO;
import com.example.documentmanagementsystem.modules.system.entity.SysMenu;
import com.example.documentmanagementsystem.modules.system.entity.SysRoleMenu;
import com.example.documentmanagementsystem.modules.system.entity.SysUser;
import com.example.documentmanagementsystem.modules.system.mapper.SysMenuMapper;
import com.example.documentmanagementsystem.modules.system.mapper.SysRoleMenuMapper;
import com.example.documentmanagementsystem.modules.system.mapper.SysUserMapper;
import com.example.documentmanagementsystem.modules.system.service.ISysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统菜单服务实现
 */
@Slf4j
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public List<SysMenu> listMenuTree() {
        List<SysMenu> allMenus = this.list(new LambdaQueryWrapper<SysMenu>()
                .orderByAsc(SysMenu::getSort));
        return buildTree(allMenus, 0L);
    }

    @Override
    public void createMenu(MenuDTO dto) {
        SysMenu menu = new SysMenu();
        BeanUtils.copyProperties(dto, menu);
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        if (menu.getStatus() == null) {
            menu.setStatus(1);
        }
        if (menu.getSort() == null) {
            menu.setSort(0);
        }
        this.save(menu);
        log.info("新增菜单: menuName={}, menuType={}", menu.getMenuName(), menu.getMenuType());
    }

    @Override
    public void updateMenu(MenuDTO dto) {
        if (dto.getId() == null) {
            throw new ServiceException("菜单ID不能为空");
        }
        SysMenu exist = this.getById(dto.getId());
        if (exist == null) {
            throw new ServiceException("菜单不存在");
        }
        // 不能把自己设为自己的父级
        if (dto.getParentId() != null && dto.getParentId().equals(dto.getId())) {
            throw new ServiceException("父菜单不能是自己");
        }
        SysMenu menu = new SysMenu();
        BeanUtils.copyProperties(dto, menu);
        this.updateById(menu);
        log.info("编辑菜单: id={}, menuName={}", dto.getId(), dto.getMenuName());
    }

    @Override
    public void deleteMenu(Long id) {
        SysMenu exist = this.getById(id);
        if (exist == null) {
            throw new ServiceException("菜单不存在");
        }
        // 有子菜单时禁止删除
        Long childCount = this.count(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getParentId, id));
        if (childCount != null && childCount > 0) {
            throw new ServiceException("存在子菜单，请先删除子菜单");
        }
        // 删除角色菜单关联
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getMenuId, id));
        this.removeById(id);
        log.info("删除菜单: id={}, menuName={}", id, exist.getMenuName());
    }

    @Override
    public List<Long> listMenuIdsByRole(Long roleId) {
        return sysRoleMenuMapper.selectList(new LambdaQueryWrapper<SysRoleMenu>()
                        .eq(SysRoleMenu::getRoleId, roleId))
                .stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenusToRole(Long roleId, List<Long> menuIds) {
        // 1. 删除角色原有菜单关联
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getRoleId, roleId));
        // 2. 批量插入新关联
        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                sysRoleMenuMapper.insert(roleMenu);
            }
        }
        log.info("角色分配菜单: roleId={}, menuIds={}", roleId, menuIds);
    }

    @Override
    public List<SysMenu> listUserMenuTree(Long userId) {
        List<SysMenu> userMenus = listUserMenus(userId);
        return buildTree(userMenus, 0L);
    }

    @Override
    public List<String> listUserPerms(Long userId) {
        List<SysMenu> userMenus = listAllUserMenus(userId);
        return userMenus.stream()
                .map(SysMenu::getPerms)
                .filter(perms -> perms != null && !perms.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * 查询用户可见的所有菜单（含按钮，权限码在按钮上）
     */
    private List<SysMenu> listAllUserMenus(Long userId) {
        // 1. 查用户角色
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || user.getRoleId() == null) {
            return Collections.emptyList();
        }
        // 2. 查角色关联的菜单ID
        List<Long> menuIds = listMenuIdsByRole(user.getRoleId());
        if (menuIds.isEmpty()) {
            return Collections.emptyList();
        }
        // 3. 查菜单详情（启用状态，含按钮类型）
        return this.list(new LambdaQueryWrapper<SysMenu>()
                .in(SysMenu::getId, menuIds)
                .eq(SysMenu::getStatus, 1)
                .orderByAsc(SysMenu::getSort));
    }

    /**
     * 查询用户可见的菜单列表（菜单树用，不含按钮）
     */
    private List<SysMenu> listUserMenus(Long userId) {
        return listAllUserMenus(userId).stream()
                .filter(menu -> menu.getMenuType() != null && menu.getMenuType() <= 2)
                .collect(Collectors.toList());
    }

    /**
     * 构建树形结构
     *
     * @param allMenus 全部菜单
     * @param parentId 父ID
     * @return 子节点列表
     */
    private List<SysMenu> buildTree(List<SysMenu> allMenus, Long parentId) {
        List<SysMenu> tree = new ArrayList<>();
        for (SysMenu menu : allMenus) {
            Long pid = menu.getParentId() == null ? 0L : menu.getParentId();
            if (pid.equals(parentId)) {
                menu.setChildren(buildTree(allMenus, menu.getId()));
                tree.add(menu);
            }
        }
        return tree;
    }
}
