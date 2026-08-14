package com.example.documentmanagementsystem.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.documentmanagementsystem.common.exception.ServiceException;
import com.example.documentmanagementsystem.modules.system.dto.RoleDTO;
import com.example.documentmanagementsystem.modules.system.dto.RoleQuery;
import com.example.documentmanagementsystem.modules.system.entity.SysRole;
import com.example.documentmanagementsystem.modules.system.entity.SysUser;
import com.example.documentmanagementsystem.modules.system.mapper.SysRoleMapper;
import com.example.documentmanagementsystem.modules.system.mapper.SysUserMapper;
import com.example.documentmanagementsystem.modules.system.service.ISysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 系统角色服务实现
 */
@Slf4j
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public IPage<SysRole> pageRoles(RoleQuery query) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        // 按角色名称模糊查询
        if (StringUtils.hasText(query.getRoleName())) {
            wrapper.like(SysRole::getRoleName, query.getRoleName());
        }
        // 按状态筛选
        if (query.getStatus() != null) {
            wrapper.eq(SysRole::getStatus, query.getStatus());
        }
        // 按创建时间倒序
        wrapper.orderByDesc(SysRole::getCreateTime);
        return this.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
    }

    @Override
    public List<SysRole> listEnabledRoles() {
        return this.list(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getStatus, 1)
                .orderByAsc(SysRole::getSort));
    }

    @Override
    public void createRole(RoleDTO dto) {
        // 角色编码唯一性校验
        checkRoleCodeUnique(dto.getRoleCode(), null);
        SysRole role = new SysRole();
        BeanUtils.copyProperties(dto, role);
        if (role.getStatus() == null) {
            role.setStatus(1);
        }
        this.save(role);
        log.info("新增角色: roleCode={}, roleName={}", role.getRoleCode(), role.getRoleName());
    }

    @Override
    public void updateRole(RoleDTO dto) {
        if (dto.getId() == null) {
            throw new ServiceException("角色ID不能为空");
        }
        SysRole exist = this.getById(dto.getId());
        if (exist == null) {
            throw new ServiceException("角色不存在");
        }
        // 角色编码唯一性校验（排除自身）
        checkRoleCodeUnique(dto.getRoleCode(), dto.getId());
        SysRole role = new SysRole();
        BeanUtils.copyProperties(dto, role);
        this.updateById(role);
        log.info("编辑角色: id={}, roleCode={}", role.getId(), role.getRoleCode());
    }

    @Override
    public void deleteRole(Long id) {
        SysRole exist = this.getById(id);
        if (exist == null) {
            throw new ServiceException("角色不存在");
        }
        // 校验是否有用户引用该角色
        Long userCount = sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getRoleId, id));
        if (userCount != null && userCount > 0) {
            throw new ServiceException("该角色下存在 " + userCount + " 个用户，无法删除");
        }
        this.removeById(id);
        log.info("删除角色: id={}, roleCode={}", id, exist.getRoleCode());
    }

    /**
     * 角色编码唯一性校验
     *
     * @param roleCode 角色编码
     * @param excludeId 排除的ID（编辑时传自身ID，新增时传 null）
     */
    private void checkRoleCodeUnique(String roleCode, Long excludeId) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, roleCode);
        if (excludeId != null) {
            wrapper.ne(SysRole::getId, excludeId);
        }
        Long count = this.count(wrapper);
        if (count != null && count > 0) {
            throw new ServiceException("角色编码已存在：" + roleCode);
        }
    }
}
