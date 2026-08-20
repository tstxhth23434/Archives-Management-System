package com.example.documentmanagementsystem.modules.dashboard.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.documentmanagementsystem.common.base.BaseController;
import com.example.documentmanagementsystem.common.result.Result;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveFonds;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveType;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveVolume;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveFile;
import com.example.documentmanagementsystem.modules.archive.entity.ElectronicFile;
import com.example.documentmanagementsystem.modules.archive.mapper.ArchiveFondsMapper;
import com.example.documentmanagementsystem.modules.archive.mapper.ArchiveTypeMapper;
import com.example.documentmanagementsystem.modules.archive.mapper.ArchiveVolumeMapper;
import com.example.documentmanagementsystem.modules.archive.mapper.ArchiveFileMapper;
import com.example.documentmanagementsystem.modules.archive.mapper.ElectronicFileMapper;
import com.example.documentmanagementsystem.modules.system.entity.SysUser;
import com.example.documentmanagementsystem.modules.system.mapper.SysUserMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 首页驾驶舱统计接口（真实数据，D15 完善）
 */
@Api(tags = "13-首页统计")
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController extends BaseController {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private ArchiveFondsMapper fondsMapper;

    @Resource
    private ArchiveTypeMapper typeMapper;

    @Resource
    private ArchiveVolumeMapper volumeMapper;

    @Resource
    private ArchiveFileMapper fileMapper;

    @Resource
    private ElectronicFileMapper electronicFileMapper;

    @ApiOperation("首页统计（用户/全宗/门类/案卷/文件/原文 真实计数，登录即可）")
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();
        // @TableLogic 逻辑删除会自动过滤 del_flag=0
        data.put("userCount", sysUserMapper.selectCount(new QueryWrapper<>()));
        data.put("fondsCount", fondsMapper.selectCount(new QueryWrapper<>()));
        data.put("typeCount", typeMapper.selectCount(new QueryWrapper<>()));
        data.put("volumeCount", volumeMapper.selectCount(new QueryWrapper<>()));
        data.put("fileCount", fileMapper.selectCount(new QueryWrapper<>()));
        data.put("electronicCount", electronicFileMapper.selectCount(new QueryWrapper<>()));
        return success(data);
    }
}
