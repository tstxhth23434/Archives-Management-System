package com.example.documentmanagementsystem.modules.archive.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.documentmanagementsystem.modules.archive.entity.ElectronicFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 电子原文服务接口（D12 上传/列表/下载/删除）
 */
public interface IElectronicFileService extends IService<ElectronicFile> {

    /**
     * 上传电子原文（校验类型/大小，按 年/月 分目录存储）
     */
    ElectronicFile upload(Long archiveId, MultipartFile file) throws IOException;

    /**
     * 查询某档案下的原文列表
     */
    List<ElectronicFile> listByArchive(Long archiveId);

    /**
     * 获取原文对应的磁盘文件（下载）
     */
    java.io.File getDiskFile(ElectronicFile record);

    /**
     * 删除原文（删库 + 删磁盘文件）
     */
    void deleteElectronic(Long id);
}
