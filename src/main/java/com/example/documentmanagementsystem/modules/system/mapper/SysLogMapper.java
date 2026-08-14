package com.example.documentmanagementsystem.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.documentmanagementsystem.modules.system.entity.SysLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统操作日志 Mapper
 */
@Mapper
public interface SysLogMapper extends BaseMapper<SysLog> {
}
