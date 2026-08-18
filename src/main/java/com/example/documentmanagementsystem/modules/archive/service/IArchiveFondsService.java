package com.example.documentmanagementsystem.modules.archive.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveFondsDTO;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveFondsQuery;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveFonds;

import java.util.List;

/**
 * 档案全宗服务接口
 */
public interface IArchiveFondsService extends IService<ArchiveFonds> {

    /**
     * 分页查询全宗
     */
    IPage<ArchiveFonds> pageFonds(ArchiveFondsQuery query);

    /**
     * 查询全部启用全宗（下拉框用）
     */
    List<ArchiveFonds> listEnabledFonds();

    /**
     * 新增全宗
     */
    void createFonds(ArchiveFondsDTO dto);

    /**
     * 编辑全宗
     */
    void updateFonds(ArchiveFondsDTO dto);

    /**
     * 删除全宗（有门类引用时禁止删除）
     */
    void deleteFonds(Long id);
}
