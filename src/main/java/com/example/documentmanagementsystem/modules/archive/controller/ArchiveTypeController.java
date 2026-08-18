package com.example.documentmanagementsystem.modules.archive.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.documentmanagementsystem.common.annotation.OpLog;
import com.example.documentmanagementsystem.common.base.BaseController;
import com.example.documentmanagementsystem.common.result.Result;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveTypeDTO;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveTypeQuery;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveType;
import com.example.documentmanagementsystem.modules.archive.service.IArchiveTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 门类管理接口
 */
@Api(tags = "08-门类管理")
@RestController
@RequestMapping("/api/archive/type")
public class ArchiveTypeController extends BaseController {

    @Resource
    private IArchiveTypeService typeService;

    @ApiOperation("分页查询门类")
    @SaCheckPermission("archive:type:query")
    @GetMapping("/page")
    public Result<IPage<ArchiveType>> page(ArchiveTypeQuery query) {
        return success(typeService.pageTypes(query));
    }

    @ApiOperation("按全宗查询门类（下拉框用）")
    @SaCheckPermission("archive:type:query")
    @GetMapping("/list")
    public Result<List<ArchiveType>> list(@ApiParam("全宗ID（为空返回全部启用门类）") @RequestParam(required = false) Long fondsId) {
        return success(typeService.listByFonds(fondsId));
    }

    @ApiOperation("新增门类")
    @SaCheckPermission("archive:type:add")
    @OpLog("新增门类")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody ArchiveTypeDTO dto) {
        typeService.createType(dto);
        return success("新增成功", null);
    }

    @ApiOperation("编辑门类")
    @SaCheckPermission("archive:type:edit")
    @OpLog("编辑门类")
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody ArchiveTypeDTO dto) {
        typeService.updateType(dto);
        return success("修改成功", null);
    }

    @ApiOperation("删除门类")
    @SaCheckPermission("archive:type:delete")
    @OpLog("删除门类")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        typeService.deleteType(id);
        return success("删除成功", null);
    }
}
