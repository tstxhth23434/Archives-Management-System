package com.example.documentmanagementsystem.modules.archive.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.documentmanagementsystem.common.annotation.OpLog;
import com.example.documentmanagementsystem.common.base.BaseController;
import com.example.documentmanagementsystem.common.result.Result;
import com.example.documentmanagementsystem.modules.archive.entity.ElectronicFile;
import com.example.documentmanagementsystem.modules.archive.service.IElectronicFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 电子原文接口（D12 上传/列表/下载/删除）
 */
@Api(tags = "12-电子原文")
@RestController
@RequestMapping("/api/archive/electronic")
public class ElectronicFileController extends BaseController {

    @Resource
    private IElectronicFileService electronicFileService;

    @ApiOperation("上传电子原文")
    @SaCheckPermission("archive:file:edit")
    @OpLog("上传电子原文")
    @PostMapping("/upload")
    public Result<ElectronicFile> upload(@ApiParam("档案ID") @RequestParam Long archiveId,
                                         @ApiParam("文件") @RequestParam("file") MultipartFile file) throws IOException {
        return success("上传成功", electronicFileService.upload(archiveId, file));
    }

    @ApiOperation("查询某档案的原文列表")
    @SaCheckPermission("archive:file:query")
    @GetMapping("/list")
    public Result<List<ElectronicFile>> list(@ApiParam("档案ID") @RequestParam Long archiveId) {
        return success(electronicFileService.listByArchive(archiveId));
    }

    @ApiOperation("在线预览电子原文（图片/pdf 浏览器内展示）")
    @SaCheckPermission("archive:file:query")
    @GetMapping("/preview/{id}")
    public ResponseEntity<FileSystemResource> preview(@PathVariable Long id) {
        ElectronicFile record = electronicFileService.getById(id);
        if (record == null) {
            throw new com.example.documentmanagementsystem.common.exception.ServiceException("电子原文不存在");
        }
        File file = electronicFileService.getDiskFile(record);
        if (!file.exists()) {
            throw new com.example.documentmanagementsystem.common.exception.ServiceException("磁盘文件不存在");
        }
        MediaType mediaType = MediaTypeFactory.getMediaType(file.getName()).orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(file.length())
                .body(new FileSystemResource(file));
    }

    @ApiOperation("下载电子原文")
    @SaCheckPermission("archive:file:query")
    @GetMapping("/download/{id}")
    public ResponseEntity<FileSystemResource> download(@PathVariable Long id) throws IOException {
        ElectronicFile record = electronicFileService.getById(id);
        if (record == null) {
            throw new com.example.documentmanagementsystem.common.exception.ServiceException("电子原文不存在");
        }
        File file = electronicFileService.getDiskFile(record);
        if (!file.exists()) {
            throw new com.example.documentmanagementsystem.common.exception.ServiceException("磁盘文件不存在");
        }
        String encodedName = URLEncoder.encode(record.getFileName(), StandardCharsets.UTF_8.name()).replace("+", "%20");
        // 双写 filename（ASCII 回退）+ filename*（UTF-8），兼容老浏览器/下载工具
        String asciiName = "download." + (record.getFileSuffix() != null ? record.getFileSuffix() : "bin");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + asciiName + "\"; filename*=UTF-8''" + encodedName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(new FileSystemResource(file));
    }

    @ApiOperation("删除电子原文")
    @SaCheckPermission("archive:file:delete")
    @OpLog("删除电子原文")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        electronicFileService.deleteElectronic(id);
        return success("删除成功", null);
    }
}
