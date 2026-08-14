package com.example.documentmanagementsystem.common.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 配置
 * 注册登录拦截器：除白名单接口外，所有请求都需要登录
 *
 * 注意：D3 登录模块完善后，这里需要配置具体的路由拦截规则。
 * 当前先占位，后续补充白名单（如 /api/auth/login、/api/auth/register 等）。
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /**
     * 注册 Sa-Token 拦截器
     * 校验所有接口是否已登录（白名单除外）
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 登录校验拦截器
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                // 拦截所有请求
                .addPathPatterns("/**")
                // 白名单：Swagger/Knife4j 文档、登录相关接口、静态资源
                .excludePathPatterns(
                        "/doc.html",
                        "/webjars/**",
                        "/swagger-resources/**",
                        // Knife4j 3.0.3 使用 Swagger 2，接口文档路径是 /v2/api-docs
                        "/v2/api-docs/**",
                        "/v3/api-docs/**",
                        "/favicon.ico",
                        "/error",
                        "/api/auth/**",
                        "/uploads/**"
                );
    }
}
