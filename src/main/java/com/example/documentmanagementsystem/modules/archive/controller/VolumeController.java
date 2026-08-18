package com.example.documentmanagementsystem.modules.archive.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.documentmanagementsystem.common.annotation.OpLog;
import com.example.documentmanagementsystem.common.base.BaseController;
import com.example.documentmanagementsystem.common.result.Result;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveVolumeDTO;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveVolumeQuery;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveVolume;
import com.example.documentmanagementsystem.modules.archive.service.IArchiveVolumeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 案卷接口（D9 只读查询；D10 完整 CRUD + 档号自动生成）
 */
@Api(tags = "09-案卷管理")
@RestController
@RequestMapping("/api/archive/volume")
public class VolumeController extends BaseController {

    @Resource
    private IArchiveVolumeService volumeService;

    @ApiOperation("分页查询案卷")
    @SaCheckPermission("archive:volume:query")
    @GetMapping("/page")
    public Result<IPage<ArchiveVolume>> page(ArchiveVolumeQuery query) {
        return success(volumeService.pageVolumes(query));
    }

    @ApiOperation("新增案卷（自动生成档号）")
    @SaCheckPermission("archive:volume:add")
    @OpLog("新增案卷")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody ArchiveVolumeDTO dto) {
        volumeService.createVolume(dto);
        return success("新增成功", null);
    }

    @ApiOperation("编辑案卷")
    @SaCheckPermission("archive:volume:edit")
    @OpLog("编辑案卷")
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody ArchiveVolumeDTO dto) {
        volumeService.updateVolume(dto);
        return success("修改成功", null);
    }

    @ApiOperation("删除案卷（有文件引用时禁止）")
    @SaCheckPermission("archive:volume:delete")
    @OpLog("删除案卷")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        volumeService.deleteVolume(id);
        return success("删除成功", null);
    }
}
