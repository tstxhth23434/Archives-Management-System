package com.example.documentmanagementsystem.modules.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.documentmanagementsystem.common.constant.PasswordConstants;
import com.example.documentmanagementsystem.common.exception.ServiceException;
import com.example.documentmanagementsystem.modules.system.dto.ChangePasswordDTO;
import com.example.documentmanagementsystem.modules.system.dto.LoginDTO;
import com.example.documentmanagementsystem.modules.system.dto.UserDTO;
import com.example.documentmanagementsystem.modules.system.dto.UserQuery;
import com.example.documentmanagementsystem.modules.system.entity.SysRole;
import com.example.documentmanagementsystem.modules.system.entity.SysUser;
import com.example.documentmanagementsystem.modules.system.mapper.SysRoleMapper;
import com.example.documentmanagementsystem.modules.system.mapper.SysUserMapper;
import com.example.documentmanagementsystem.modules.system.service.ISysUserService;
import com.example.documentmanagementsystem.modules.system.vo.LoginVO;
import com.example.documentmanagementsystem.modules.system.vo.UserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * 系统用户服务实现
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    /**
     * 超级管理员用户ID（保护账号，不允许删除/禁用）
     */
    private static final Long ADMIN_USER_ID = 1L;

    /**
     * 重置密码后的默认密码
     */
    private static final String DEFAULT_PASSWORD = "admin123";

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Override
    public LoginVO login(LoginDTO dto, String ip) {
        // 1. 根据用户名查询用户（del_flag=0 由 MyBatis-Plus 逻辑删除自动过滤）
        SysUser user = this.getOne(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (user == null) {
            throw new ServiceException("用户名或密码错误");
        }

        // 2. 校验密码（BCrypt 校验）
        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new ServiceException("用户名或密码错误");
        }

        // 3. 校验账号状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new ServiceException("账号已被禁用，请联系管理员");
        }

        // 4. Sa-Token 登录，签发 token
        StpUtil.login(user.getId());
        // 将用户名存入会话，供操作日志切面（OpLogAspect）读取
        StpUtil.getSession().set("username", user.getUsername());

        // 5. 更新最后登录信息
        SysUser updateUser = new SysUser();
        updateUser.setId(user.getId());
        updateUser.setLoginIp(ip);
        updateUser.setLoginTime(LocalDateTime.now());
        this.updateById(updateUser);
        log.info("用户登录成功: username={}, ip={}", dto.getUsername(), ip);

        // 6. 组装返回结果
        LoginVO vo = new LoginVO();
        vo.setToken(StpUtil.getTokenValue());
        vo.setUserInfo(this.convertToUserInfoVO(user));
        return vo;
    }

    @Override
    public void logout() {
        StpUtil.logout();
        log.info("用户退出登录: loginId={}", StpUtil.getLoginIdDefaultNull());
    }

    @Override
    public UserInfoVO getCurrentUserInfo() {
        // 获取当前登录用户 ID（拦截器已保证登录态）
        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new ServiceException(401, "用户不存在或已被删除");
        }
        return this.convertToUserInfoVO(user);
    }

    @Override
    public void changePassword(ChangePasswordDTO dto) {
        // 1. 获取当前登录用户
        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new ServiceException(401, "用户不存在或已被删除");
        }

        // 2. 校验旧密码
        if (!BCrypt.checkpw(dto.getOldPassword(), user.getPassword())) {
            throw new ServiceException("原密码错误");
        }

        // 3. 新密码不能与原密码相同
        if (BCrypt.checkpw(dto.getNewPassword(), user.getPassword())) {
            throw new ServiceException("新密码不能与原密码相同");
        }

        // 4. 更新密码（BCrypt 加密）
        SysUser updateUser = new SysUser();
        updateUser.setId(userId);
        updateUser.setPassword(BCrypt.hashpw(dto.getNewPassword()));
        this.updateById(updateUser);
        log.info("用户修改密码: userId={}", userId);

        // 5. 强制下线，让用户用新密码重新登录
        StpUtil.logout();
    }

    @Override
    public IPage<SysUser> pageUsers(UserQuery query) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        // 条件筛选
        if (StringUtils.hasText(query.getUsername())) {
            wrapper.like(SysUser::getUsername, query.getUsername());
        }
        if (StringUtils.hasText(query.getRealName())) {
            wrapper.like(SysUser::getRealName, query.getRealName());
        }
        if (query.getRoleId() != null) {
            wrapper.eq(SysUser::getRoleId, query.getRoleId());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, query.getStatus());
        }
        // 按创建时间倒序
        wrapper.orderByDesc(SysUser::getCreateTime);
        IPage<SysUser> page = this.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        // 密码脱敏：查询结果不返回密码
        page.getRecords().forEach(user -> user.setPassword(null));
        return page;
    }

    @Override
    public void createUser(UserDTO dto) {
        // 1. 用户名唯一性校验
        checkUsernameUnique(dto.getUsername(), null);
        // 2. 密码必填 + 格式校验
        if (!StringUtils.hasText(dto.getPassword())) {
            throw new ServiceException("密码不能为空");
        }
        validatePassword(dto.getPassword());
        // 3. 角色存在性校验
        checkRoleExists(dto.getRoleId());
        // 4. 组装实体
        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        this.save(user);
        log.info("新增用户: username={}, roleId={}", user.getUsername(), user.getRoleId());
    }

    @Override
    public void updateUser(UserDTO dto) {
        if (dto.getId() == null) {
            throw new ServiceException("用户ID不能为空");
        }
        SysUser exist = this.getById(dto.getId());
        if (exist == null) {
            throw new ServiceException("用户不存在");
        }
        // 用户名唯一性校验（排除自身）
        checkUsernameUnique(dto.getUsername(), dto.getId());
        // 角色存在性校验
        checkRoleExists(dto.getRoleId());
        // 组装实体：只更新允许修改的字段
        SysUser user = new SysUser();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setRealName(dto.getRealName());
        user.setRoleId(dto.getRoleId());
        user.setFondsId(dto.getFondsId());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setStatus(dto.getStatus());
        // 密码字段：编辑时传了密码才更新
        if (StringUtils.hasText(dto.getPassword())) {
            validatePassword(dto.getPassword());
            user.setPassword(BCrypt.hashpw(dto.getPassword()));
        }
        this.updateById(user);
        log.info("编辑用户: id={}, username={}", dto.getId(), dto.getUsername());
    }

    @Override
    public void deleteUser(Long id) {
        SysUser exist = this.getById(id);
        if (exist == null) {
            throw new ServiceException("用户不存在");
        }
        // 保护：不能删除超级管理员账号
        if (ADMIN_USER_ID.equals(id)) {
            throw new ServiceException("超级管理员账号不允许删除");
        }
        // 保护：不能删除自己
        Long currentUserId = StpUtil.getLoginIdAsLong();
        if (currentUserId.equals(id)) {
            throw new ServiceException("不能删除当前登录账号");
        }
        // 逻辑删除（del_flag=1）
        this.removeById(id);
        log.info("删除用户: id={}, username={}", id, exist.getUsername());
    }

    @Override
    public void changeUserStatus(Long id, Integer status) {
        SysUser exist = this.getById(id);
        if (exist == null) {
            throw new ServiceException("用户不存在");
        }
        // 保护：超级管理员账号不允许禁用
        if (ADMIN_USER_ID.equals(id)) {
            throw new ServiceException("超级管理员账号不允许禁用");
        }
        // 保护：不能禁用自己
        Long currentUserId = StpUtil.getLoginIdAsLong();
        if (currentUserId.equals(id)) {
            throw new ServiceException("不能禁用当前登录账号");
        }
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        this.updateById(user);
        log.info("{}用户: id={}, username={}",
                status != null && status == 1 ? "启用" : "禁用", id, exist.getUsername());
    }

    @Override
    public void resetPassword(Long id) {
        SysUser exist = this.getById(id);
        if (exist == null) {
            throw new ServiceException("用户不存在");
        }
        SysUser user = new SysUser();
        user.setId(id);
        user.setPassword(BCrypt.hashpw(DEFAULT_PASSWORD));
        this.updateById(user);
        log.info("重置用户密码: id={}, username={}", id, exist.getUsername());
    }

    /**
     * 用户名唯一性校验
     */
    private void checkUsernameUnique(String username, Long excludeId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username);
        if (excludeId != null) {
            wrapper.ne(SysUser::getId, excludeId);
        }
        Long count = this.count(wrapper);
        if (count != null && count > 0) {
            throw new ServiceException("用户名已存在：" + username);
        }
    }

    /**
     * 密码格式校验（复用 PasswordConstants 正则）
     */
    private void validatePassword(String password) {
        if (!Pattern.matches(PasswordConstants.PASSWORD_REGEX, password)) {
            throw new ServiceException(PasswordConstants.PASSWORD_MESSAGE);
        }
    }

    /**
     * 角色存在性校验
     */
    private void checkRoleExists(Long roleId) {
        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null) {
            throw new ServiceException("所选角色不存在");
        }
    }

    /**
     * 实体转 VO（脱敏，不暴露密码）
     */
    private UserInfoVO convertToUserInfoVO(SysUser user) {
        UserInfoVO vo = new UserInfoVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
