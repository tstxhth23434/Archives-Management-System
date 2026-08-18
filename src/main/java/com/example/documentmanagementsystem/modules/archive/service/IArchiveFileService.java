package com.example.documentmanagementsystem.modules.archive.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveFileDTO;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveFileQuery;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveFile;

/**
 * 档案文件服务接口（D10 著录）
 */
public interface IArchiveFileService extends IService<ArchiveFile> {

    /**
     * 分页查询文件（按全宗/门类/案卷/年度/题名过滤）
     */
    IPage<ArchiveFile> pageFiles(ArchiveFileQuery query);

    /**
     * 新增文件（自动生成档号：全宗号-门类代码-年度-四位序号）
     */
    void createFile(ArchiveFileDTO dto);

    /**
     * 编辑文件（全宗/门类/年度不可改，档号不变）
     */
    void updateFile(ArchiveFileDTO dto);

    /**
     * 删除文件（逻辑删除）
     */
    void deleteFile(Long id);
}
