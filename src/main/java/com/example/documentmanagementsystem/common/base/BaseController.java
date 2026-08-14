package com.example.documentmanagementsystem.common.base;

import com.example.documentmanagementsystem.common.result.Result;

/**
 * Controller 基类
 * 封装常用 success / error 方法，减少 Controller 重复代码
 */
public class BaseController {

    /**
     * 成功返回（无数据）
     */
    protected <T> Result<T> success() {
        return Result.success();
    }

    /**
     * 成功返回（带数据）
     */
    protected <T> Result<T> success(T data) {
        return Result.success(data);
    }

    /**
     * 成功返回（自定义消息 + 数据）
     */
    protected <T> Result<T> success(String message, T data) {
        return Result.success(message, data);
    }

    /**
     * 失败返回（默认消息）
     */
    protected <T> Result<T> error() {
        return Result.error();
    }

    /**
     * 失败返回（自定义消息）
     */
    protected <T> Result<T> error(String message) {
        return Result.error(message);
    }

    /**
     * 失败返回（自定义状态码 + 消息）
     */
    protected <T> Result<T> error(Integer code, String message) {
        return Result.error(code, message);
    }
}
