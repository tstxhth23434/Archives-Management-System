package com.example.documentmanagementsystem.modules.archive.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.documentmanagementsystem.common.base.BaseController;
import com.example.documentmanagementsystem.common.result.Result;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveTreeNode;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveFonds;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveType;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveVolume;
import com.example.documentmanagementsystem.modules.archive.mapper.ArchiveVolumeMapper;
import com.example.documentmanagementsystem.modules.archive.service.IArchiveFondsService;
import com.example.documentmanagementsystem.modules.archive.service.IArchiveTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 档案树接口（D9）：全宗 → 门类 → 年度 三级
 * 年度数据来自案卷表 da_volume（按门类聚合 distinct year）
 */
@Api(tags = "10-档案树")
@RestController
@RequestMapping("/api/archive/tree")
public class ArchiveTreeController extends BaseController {

    @Resource
    private IArchiveFondsService fondsService;

    @Resource
    private IArchiveTypeService typeService;

    @Resource
    private ArchiveVolumeMapper volumeMapper;

    @ApiOperation("查询档案树（全宗→门类→年度）")
    @SaCheckPermission("archive:volume:query")
    @GetMapping
    public Result<List<ArchiveTreeNode>> tree() {
        List<ArchiveTreeNode> roots = new java.util.ArrayList<>();
        // 第一级：启用中的全宗
        for (ArchiveFonds fonds : fondsService.listEnabledFonds()) {
            ArchiveTreeNode fondsNode = new ArchiveTreeNode();
            fondsNode.setId(fonds.getId());
            fondsNode.setNodeKey("fonds-" + fonds.getId());
            fondsNode.setLabel(fonds.getFondsName());
            fondsNode.setType("fonds");
            // 第二级：门类
            for (ArchiveType type : typeService.listByFonds(fonds.getId())) {
                ArchiveTreeNode typeNode = new ArchiveTreeNode();
                typeNode.setId(type.getId());
                typeNode.setNodeKey("type-" + type.getId());
                typeNode.setLabel(type.getTypeName());
                typeNode.setType("type");
                // 第三级：年度（案卷表中已存在；nodeKey 带门类前缀避免跨门类重复）
                for (Integer year : volumeMapper.listYearsByType(type.getId())) {
                    ArchiveTreeNode yearNode = new ArchiveTreeNode();
                    yearNode.setId(year.longValue());
                    yearNode.setNodeKey("year-" + type.getId() + "-" + year);
                    yearNode.setLabel(year + "年");
                    yearNode.setType("year");
                    typeNode.getChildren().add(yearNode);
                }
                fondsNode.getChildren().add(typeNode);
            }
            roots.add(fondsNode);
        }
        return success(roots);
    }
}
