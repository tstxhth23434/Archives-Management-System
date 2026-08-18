package com.example.documentmanagementsystem.modules.archive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.documentmanagementsystem.common.exception.ServiceException;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveFileDTO;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveFileQuery;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveFonds;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveFile;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveType;
import com.example.documentmanagementsystem.modules.archive.mapper.ArchiveFileMapper;
import com.example.documentmanagementsystem.modules.archive.mapper.ArchiveFondsMapper;
import com.example.documentmanagementsystem.modules.archive.mapper.ArchiveTypeMapper;
import com.example.documentmanagementsystem.modules.archive.service.IArchiveFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 档案文件服务实现（D10 著录 + 档号自动生成）
 */
@Slf4j
@Service
public class ArchiveFileServiceImpl extends ServiceImpl<ArchiveFileMapper, ArchiveFile> implements IArchiveFileService {

    private final ArchiveFondsMapper fondsMapper;
    private final ArchiveTypeMapper typeMapper;

    public ArchiveFileServiceImpl(ArchiveFondsMapper fondsMapper, ArchiveTypeMapper typeMapper) {
        this.fondsMapper = fondsMapper;
        this.typeMapper = typeMapper;
    }

    @Override
    public IPage<ArchiveFile> pageFiles(ArchiveFileQuery query) {
        LambdaQueryWrapper<ArchiveFile> wrapper = new LambdaQueryWrapper<>();
        if (query.getFondsId() != null) {
            wrapper.eq(ArchiveFile::getFondsId, query.getFondsId());
        }
        if (query.getTypeId() != null) {
            wrapper.eq(ArchiveFile::getTypeId, query.getTypeId());
        }
        if (query.getVolumeId() != null) {
            wrapper.eq(ArchiveFile::getVolumeId, query.getVolumeId());
        }
        if (query.getYear() != null) {
            wrapper.eq(ArchiveFile::getYear, query.getYear());
        }
        if (StringUtils.hasText(query.getTitle())) {
            wrapper.like(ArchiveFile::getTitle, query.getTitle());
        }
        wrapper.orderByDesc(ArchiveFile::getCreateTime);
        return this.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createFile(ArchiveFileDTO dto) {
        // 全宗/门类必须存在（档号需要全宗号与门类代码）
        ArchiveFonds fonds = fondsMapper.selectById(dto.getFondsId());
        if (fonds == null) {
            throw new ServiceException("全宗不存在");
        }
        ArchiveType type = typeMapper.selectById(dto.getTypeId());
        if (type == null) {
            throw new ServiceException("门类不存在");
        }
        if (!Objects.equals(type.getFondsId(), dto.getFondsId())) {
            throw new ServiceException("门类不属于所选全宗");
        }
        ArchiveFile file = new ArchiveFile();
        BeanUtils.copyProperties(dto, file);
        // 自动生成档号：全宗号-门类代码-年度-四位序号（序号=该年度最大序号+1，档号不复用）
        file.setArchiveNo(generateArchiveNo(fonds.getFondsCode(), type.getTypeCode(), dto.getFondsId(), dto.getTypeId(), dto.getYear()));
        if (file.getStatus() == null) {
            file.setStatus(1);
        }
        this.save(file);
        log.info("新增文件: archiveNo={}, title={}", file.getArchiveNo(), file.getTitle());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFile(ArchiveFileDTO dto) {
        if (dto.getId() == null) {
            throw new ServiceException("文件ID不能为空");
        }
        ArchiveFile exist = this.getById(dto.getId());
        if (exist == null) {
            throw new ServiceException("文件不存在");
        }
        // 全宗/门类/年度参与档号，不允许修改
        if (!Objects.equals(exist.getFondsId(), dto.getFondsId())
                || !Objects.equals(exist.getTypeId(), dto.getTypeId())
                || !Objects.equals(exist.getYear(), dto.getYear())) {
            throw new ServiceException("全宗/门类/年度不可修改（档号已生成）");
        }
        ArchiveFile file = new ArchiveFile();
        BeanUtils.copyProperties(dto, file);
        // 保留原档号
        file.setArchiveNo(exist.getArchiveNo());
        this.updateById(file);
        log.info("编辑文件: id={}, archiveNo={}", file.getId(), exist.getArchiveNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(Long id) {
        ArchiveFile exist = this.getById(id);
        if (exist == null) {
            throw new ServiceException("文件不存在");
        }
        this.removeById(id);
        log.info("删除文件: id={}, archiveNo={}", id, exist.getArchiveNo());
    }

    /**
     * 生成文件档号：全宗号-门类代码-年度-四位序号
     * 序号统计不排除已删除记录（档号不复用，避免逻辑删除后重建撞唯一索引）
     */
    private String generateArchiveNo(String fondsCode, String typeCode, Long fondsId, Long typeId, Integer year) {
        Integer maxSeq = this.baseMapper.selectMaxSeq(fondsId, typeId, year);
        int next = (maxSeq == null ? 0 : maxSeq) + 1;
        return fondsCode + "-" + typeCode + "-" + year + "-" + String.format("%04d", next);
    }
}
