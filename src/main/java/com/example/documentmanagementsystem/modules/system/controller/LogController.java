package com.example.documentmanagementsystem.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.documentmanagementsystem.common.base.BaseController;
import com.example.documentmanagementsystem.common.result.Result;
import com.example.documentmanagementsystem.modules.system.entity.SysLog;
import com.example.documentmanagementsystem.modules.system.mapper.SysLogMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 操作日志查询接口
 * 日志由 OpLogAspect 切面自动写入 sys_log 表
 */
@Api(tags = "06-操作日志")
@RestController
@RequestMapping("/api/system/log")
public class LogController extends BaseController {

    @Resource
    private SysLogMapper sysLogMapper;

    @ApiOperation("分页查询操作日志")
    @GetMapping("/page")
    public Result<IPage<SysLog>> page(@RequestParam(required = false) String username,
                                      @RequestParam(required = false) String operation,
                                      @RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        // 按操作人模糊查询
        if (StringUtils.hasText(username)) {
            wrapper.like(SysLog::getUsername, username);
        }
        // 按操作描述模糊查询
        if (StringUtils.hasText(operation)) {
            wrapper.like(SysLog::getOperation, operation);
        }
        // 按时间倒序
        wrapper.orderByDesc(SysLog::getCreateTime);
        return success(sysLogMapper.selectPage(new Page<>(pageNum, pageSize), wrapper));
    }
}
