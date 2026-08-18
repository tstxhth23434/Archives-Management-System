package com.example.documentmanagementsystem.modules.archive.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveVolumeDTO;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveVolumeQuery;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveVolume;

/**
 * 档案案卷服务接口（D9 只读分页查询，D10 完整 CRUD + 档号生成）
 */
public interface IArchiveVolumeService extends IService<ArchiveVolume> {

    /**
     * 分页查询案卷（按全宗/门类/年度/题名过滤）
     */
    IPage<ArchiveVolume> pageVolumes(ArchiveVolumeQuery query);

    /**
     * 新增案卷（自动生成档号：全宗号-门类代码-年度-四位序号）
     */
    void createVolume(ArchiveVolumeDTO dto);

    /**
     * 编辑案卷（全宗/门类/年度不可改，档号不变）
     */
    void updateVolume(ArchiveVolumeDTO dto);

    /**
     * 删除案卷（有文件引用时禁止删除）
     */
    void deleteVolume(Long id);
}
