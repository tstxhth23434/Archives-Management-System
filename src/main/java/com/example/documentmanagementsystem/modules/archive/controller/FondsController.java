package com.example.documentmanagementsystem.modules.archive.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.documentmanagementsystem.common.annotation.OpLog;
import com.example.documentmanagementsystem.common.base.BaseController;
import com.example.documentmanagementsystem.common.result.Result;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveFondsDTO;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveFondsQuery;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveFonds;
import com.example.documentmanagementsystem.modules.archive.service.IArchiveFondsService;
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
import java.util.List;

/**
 * 全宗管理接口
 */
@Api(tags = "07-全宗管理")
@RestController
@RequestMapping("/api/archive/fonds")
public class FondsController extends BaseController {

    @Resource
    private IArchiveFondsService fondsService;

    @ApiOperation("分页查询全宗")
    @SaCheckPermission("archive:fonds:query")
    @GetMapping("/page")
    public Result<IPage<ArchiveFonds>> page(ArchiveFondsQuery query) {
        return success(fondsService.pageFonds(query));
    }

    @ApiOperation("查询全部启用全宗（下拉框用）")
    @SaCheckPermission("archive:fonds:query")
    @GetMapping("/list")
    public Result<List<ArchiveFonds>> list() {
        return success(fondsService.listEnabledFonds());
    }

    @ApiOperation("新增全宗")
    @SaCheckPermission("archive:fonds:add")
    @OpLog("新增全宗")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody ArchiveFondsDTO dto) {
        fondsService.createFonds(dto);
        return success("新增成功", null);
    }

    @ApiOperation("编辑全宗")
    @SaCheckPermission("archive:fonds:edit")
    @OpLog("编辑全宗")
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody ArchiveFondsDTO dto) {
        fondsService.updateFonds(dto);
        return success("修改成功", null);
    }

    @ApiOperation("删除全宗")
    @SaCheckPermission("archive:fonds:delete")
    @OpLog("删除全宗")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        fondsService.deleteFonds(id);
        return success("删除成功", null);
    }
}
