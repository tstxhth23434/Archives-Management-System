package com.example.documentmanagementsystem.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.documentmanagementsystem.modules.system.dto.RoleDTO;
import com.example.documentmanagementsystem.modules.system.dto.RoleQuery;
import com.example.documentmanagementsystem.modules.system.entity.SysRole;

import java.util.List;

/**
 * 系统角色服务
 */
public interface ISysRoleService extends IService<SysRole> {

    /**
     * 分页查询角色
     */
    IPage<SysRole> pageRoles(RoleQuery query);

    /**
     * 查询全部启用角色（下拉框用）
     */
    List<SysRole> listEnabledRoles();

    /**
     * 新增角色
     */
    void createRole(RoleDTO dto);

    /**
     * 编辑角色
     */
    void updateRole(RoleDTO dto);

    /**
     * 删除角色（有用户引用时禁止删除）
     */
    void deleteRole(Long id);
}
