package com.example.documentmanagementsystem.common.config;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 配置
 * 1. 分页插件
 * 2. 公共字段自动填充（create_by / create_time / update_by / update_time）
 */
@Slf4j
@Configuration
public class MybatisPlusConfig {

    /**
     * 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件：指定数据库类型为 MySQL
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 公共字段自动填充
     * 所有继承 BaseEntity 的实体，插入/更新时自动填充 create_by、create_time 等字段
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                // 插入时：填充 create_time、update_time
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

                // 填充 create_by / update_by（从 Sa-Token 获取当前登录用户，未登录时为 null）
                Long currentUserId = getCurrentUserId();
                if (currentUserId != null) {
                    this.strictInsertFill(metaObject, "createBy", Long.class, currentUserId);
                    this.strictInsertFill(metaObject, "updateBy", Long.class, currentUserId);
                }
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                // 更新时：只刷新 update_time
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

                // 填充 update_by
                Long currentUserId = getCurrentUserId();
                if (currentUserId != null) {
                    this.strictUpdateFill(metaObject, "updateBy", Long.class, currentUserId);
                }
            }

            /**
             * 获取当前登录用户 ID（未登录返回 null）
             */
            private Long getCurrentUserId() {
                try {
                    Object loginId = StpUtil.getLoginIdDefaultNull();
                    return loginId == null ? null : Long.valueOf(loginId.toString());
                } catch (Exception e) {
                    // 非登录态请求（如登录接口本身）不填充操作人
                    return null;
                }
            }
        };
    }
}
