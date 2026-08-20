package com.example.documentmanagementsystem.modules.archive.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveFileDTO;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveFileQuery;
import com.example.documentmanagementsystem.modules.archive.dto.ImportResultVO;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveFile;
import com.example.documentmanagementsystem.modules.archive.entity.Lifecycle;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 档案文件服务接口（D10 著录，D13 Excel 批量导入）
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

    /**
     * Excel 批量导入（逐行校验，错误行反馈，档号自动生成）
     */
    ImportResultVO importExcel(MultipartFile file) throws IOException;

    /**
     * 状态流转（整理中→已归档→已封库，顺序校验，写生命周期履历）
     */
    void changeStatus(Long id, Integer status);

    /**
     * 查询档案生命周期履历（时间正序，详情页时间轴）
     */
    List<Lifecycle> listLifecycle(Long id);
}
