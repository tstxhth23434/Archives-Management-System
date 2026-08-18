package com.example.documentmanagementsystem.modules.archive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveVolumeQuery;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveVolume;
import com.example.documentmanagementsystem.modules.archive.mapper.ArchiveVolumeMapper;
import com.example.documentmanagementsystem.modules.archive.service.IArchiveVolumeService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 档案案卷服务实现（D9 只读）
 */
@Service
public class ArchiveVolumeServiceImpl extends ServiceImpl<ArchiveVolumeMapper, ArchiveVolume> implements IArchiveVolumeService {

    @Override
    public IPage<ArchiveVolume> pageVolumes(ArchiveVolumeQuery query) {
        LambdaQueryWrapper<ArchiveVolume> wrapper = new LambdaQueryWrapper<>();
        if (query.getFondsId() != null) {
            wrapper.eq(ArchiveVolume::getFondsId, query.getFondsId());
        }
        if (query.getTypeId() != null) {
            wrapper.eq(ArchiveVolume::getTypeId, query.getTypeId());
        }
        if (query.getYear() != null) {
            wrapper.eq(ArchiveVolume::getYear, query.getYear());
        }
        if (StringUtils.hasText(query.getTitle())) {
            wrapper.like(ArchiveVolume::getTitle, query.getTitle());
        }
        wrapper.orderByDesc(ArchiveVolume::getCreateTime);
        return this.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
    }
}
