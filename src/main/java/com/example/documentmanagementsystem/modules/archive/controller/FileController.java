package com.example.documentmanagementsystem.modules.archive.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.documentmanagementsystem.common.annotation.OpLog;
import com.example.documentmanagementsystem.common.base.BaseController;
import com.example.documentmanagementsystem.common.result.Result;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveFileDTO;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveFileQuery;
import com.example.documentmanagementsystem.modules.archive.dto.ImportResultVO;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveFile;
import com.example.documentmanagementsystem.modules.archive.service.IArchiveFileService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 档案文件著录接口（D10；D13 Excel 批量导入）
 */
@Api(tags = "11-文件著录")
@RestController
@RequestMapping("/api/archive/file")
public class FileController extends BaseController {

    @Resource
    private IArchiveFileService fileService;

    @ApiOperation("分页查询文件")
    @SaCheckPermission("archive:file:query")
    @GetMapping("/page")
    public Result<IPage<ArchiveFile>> page(ArchiveFileQuery query) {
        return success(fileService.pageFiles(query));
    }

    @ApiOperation("Excel 批量导入（逐行校验，错误行反馈）")
    @SaCheckPermission("archive:file:add")
    @OpLog("批量导入文件")
    @PostMapping("/import")
    public Result<ImportResultVO> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        return success(fileService.importExcel(file));
    }

    @ApiOperation("新增文件（自动生成档号）")
    @SaCheckPermission("archive:file:add")
    @OpLog("新增文件")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody ArchiveFileDTO dto) {
        fileService.createFile(dto);
        return success("新增成功", null);
    }

    @ApiOperation("编辑文件")
    @SaCheckPermission("archive:file:edit")
    @OpLog("编辑文件")
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody ArchiveFileDTO dto) {
        fileService.updateFile(dto);
        return success("修改成功", null);
    }

    @ApiOperation("删除文件")
    @SaCheckPermission("archive:file:delete")
    @OpLog("删除文件")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        fileService.deleteFile(id);
        return success("删除成功", null);
    }
}
