package com.example.documentmanagementsystem.modules.archive.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.documentmanagementsystem.common.base.BaseController;
import com.example.documentmanagementsystem.common.result.Result;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveFileQuery;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveFile;
import com.example.documentmanagementsystem.modules.archive.service.IArchiveFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 档案检索接口（D16，普通用户也可用）
 * 独立权限码 archive:search:query，与文件管理 page 区分
 */
@Api(tags = "14-档案检索")
@RestController
@RequestMapping("/api/archive/search")
public class SearchController extends BaseController {

    @Resource
    private IArchiveFileService fileService;

    @ApiOperation("多条件检索档案（题名/档号/关键词模糊 + 年度/门类/保管期限/密级 + 排序分页）")
    @SaCheckPermission("archive:search:query")
    @GetMapping
    public Result<IPage<ArchiveFile>> search(ArchiveFileQuery query) {
        return success(fileService.pageFiles(query));
    }
}
