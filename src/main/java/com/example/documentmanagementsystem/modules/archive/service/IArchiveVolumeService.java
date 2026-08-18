package com.example.documentmanagementsystem.modules.archive.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveVolumeQuery;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveVolume;

/**
 * 档案案卷服务接口（D9 只读分页查询，CRUD/档号生成 D10）
 */
public interface IArchiveVolumeService extends IService<ArchiveVolume> {

    /**
     * 分页查询案卷（按全宗/门类/年度/题名过滤）
     */
    IPage<ArchiveVolume> pageVolumes(ArchiveVolumeQuery query);
}
