package com.example.documentmanagementsystem.common.constant;

/**
 * 密码规则常量
 * 规则：8-16 位，必须同时包含字母和数字，仅允许字母/数字字符
 */
public class PasswordConstants {

    /**
     * 密码正则：
     * (?=.*[A-Za-z])      至少一个字母
     * (?=.*\d)            至少一个数字
     * [A-Za-z\d]{8,16}    总长度 8-16 位，仅字母和数字
     */
    public static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,16}$";

    /**
     * 密码校验失败提示
     */
    public static final String PASSWORD_MESSAGE = "密码需为8-16位字母和数字组合，且不能包含特殊字符";

    private PasswordConstants() {
        // 常量类禁止实例化
    }
}
