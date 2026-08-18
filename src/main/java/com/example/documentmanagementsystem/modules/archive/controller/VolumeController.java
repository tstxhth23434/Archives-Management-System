package com.example.documentmanagementsystem.modules.archive.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.documentmanagementsystem.common.base.BaseController;
import com.example.documentmanagementsystem.common.result.Result;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveVolumeQuery;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveVolume;
import com.example.documentmanagementsystem.modules.archive.service.IArchiveVolumeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 案卷查询接口（D9 只读；CRUD/档号生成 D10 扩展）
 */
@Api(tags = "09-案卷查询")
@RestController
@RequestMapping("/api/archive/volume")
public class VolumeController extends BaseController {

    @Resource
    private IArchiveVolumeService volumeService;

    @ApiOperation("分页查询案卷（档案树右侧列表）")
    @SaCheckPermission("archive:volume:query")
    @GetMapping("/page")
    public Result<IPage<ArchiveVolume>> page(ArchiveVolumeQuery query) {
        return success(volumeService.pageVolumes(query));
    }
}
