package com.example.documentmanagementsystem.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvc 配置
 * 1. 跨域支持（前后端分离开发）
 * 2. 静态资源映射（上传文件访问）
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 跨域配置
     * 开发环境允许所有来源，生产环境需要限制具体域名
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 允许所有来源（开发环境），生产环境改为具体域名
                .allowedOriginPatterns("*")
                // 允许的请求方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许携带凭证（Cookie/Token）
                .allowCredentials(true)
                // 允许的请求头
                .allowedHeaders("*")
                // 预检请求缓存时间（秒）
                .maxAge(3600);
    }

    /**
     * 静态资源映射
     * 将 /uploads/** 映射到本地文件系统，方便直接访问上传的文件
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 文件上传路径映射（Windows 路径格式兼容）
        String uploadPath = System.getProperty("user.home") + "/dms-uploads/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);
    }
}
