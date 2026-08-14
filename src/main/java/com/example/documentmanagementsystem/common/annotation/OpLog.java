package com.example.documentmanagementsystem.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解
 * 标注在 Controller 方法上，由 OpLogAspect 切面自动记录操作日志到 sys_log 表
 *
 * 用法：@OpLog("删除用户")
 * public Result<Void> delete(@PathVariable Long id) { ... }
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OpLog {

    /**
     * 操作描述，如"删除用户"、"新增角色"
     */
    String value();
}
