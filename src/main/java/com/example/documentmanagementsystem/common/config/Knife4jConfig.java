package com.example.documentmanagementsystem.common.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Knife4j 接口文档配置
 * 访问地址：http://localhost:8080/doc.html
 *
 * Knife4j 是 Swagger 的增强 UI，提供更友好的中文接口文档界面。
 */
@Configuration
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
public class Knife4jConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                // API 文档基本信息
                .apiInfo(apiInfo())
                // 选择哪些接口暴露给 Swagger
                .select()
                // 扫描指定包下的所有 Controller
                .apis(RequestHandlerSelectors.basePackage("com.example.documentmanagementsystem.modules"))
                // 所有路径
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 文档基本信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("档案管理系统 API 文档")
                .description("Document Management System - 本科毕业设计项目后端接口")
                .contact(new Contact("开发者", "", ""))
                .version("v1.0.0")
                .build();
    }
}
