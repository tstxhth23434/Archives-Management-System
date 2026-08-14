package com.example.documentmanagementsystem.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.documentmanagementsystem.modules.system.dto.ChangePasswordDTO;
import com.example.documentmanagementsystem.modules.system.dto.LoginDTO;
import com.example.documentmanagementsystem.modules.system.dto.UserDTO;
import com.example.documentmanagementsystem.modules.system.dto.UserQuery;
import com.example.documentmanagementsystem.modules.system.entity.SysUser;
import com.example.documentmanagementsystem.modules.system.vo.LoginVO;
import com.example.documentmanagementsystem.modules.system.vo.UserInfoVO;

/**
 * 系统用户服务
 */
public interface ISysUserService extends IService<SysUser> {

    /**
     * 用户登录
     *
     * @param dto 登录参数（用户名 + 密码）
     * @param ip  登录 IP
     * @return 登录结果（token + 用户信息）
     */
    LoginVO login(LoginDTO dto, String ip);

    /**
     * 退出登录
     */
    void logout();

    /**
     * 获取当前登录用户信息
     *
     * @return 当前登录用户信息
     */
    UserInfoVO getCurrentUserInfo();

    /**
     * 修改当前登录用户密码
     *
     * @param dto 旧密码 + 新密码
     */
    void changePassword(ChangePasswordDTO dto);

    /**
     * 分页查询用户（密码脱敏）
     */
    IPage<SysUser> pageUsers(UserQuery query);

    /**
     * 新增用户
     */
    void createUser(UserDTO dto);

    /**
     * 编辑用户（password 为空表示不修改密码）
     */
    void updateUser(UserDTO dto);

    /**
     * 删除用户（不允许删除自己）
     */
    void deleteUser(Long id);

    /**
     * 启用/禁用用户（不允许禁用自己）
     */
    void changeUserStatus(Long id, Integer status);

    /**
     * 重置用户密码为默认密码
     */
    void resetPassword(Long id);
}
