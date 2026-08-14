package com.example.documentmanagementsystem.common.exception;

import lombok.Getter;

/**
 * 业务异常
 * 在 Service 层抛出，由全局异常处理器统一转成 Result 返回
 */
@Getter
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码，默认 500
     */
    private final Integer code;

    public ServiceException(String message) {
        super(message);
        this.code = 500;
    }

    public ServiceException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }
}
