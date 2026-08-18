package com.example.documentmanagementsystem.modules.archive.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveTypeDTO;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveTypeQuery;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveType;

import java.util.List;

/**
 * 档案门类服务接口
 */
public interface IArchiveTypeService extends IService<ArchiveType> {

    /**
     * 分页查询门类
     */
    IPage<ArchiveType> pageTypes(ArchiveTypeQuery query);

    /**
     * 按全宗查询门类（下拉框用）
     */
    List<ArchiveType> listByFonds(Long fondsId);

    /**
     * 新增门类
     */
    void createType(ArchiveTypeDTO dto);

    /**
     * 编辑门类
     */
    void updateType(ArchiveTypeDTO dto);

    /**
     * 删除门类
     */
    void deleteType(Long id);
}
