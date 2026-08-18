package com.example.documentmanagementsystem.modules.archive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.documentmanagementsystem.common.exception.ServiceException;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveFondsDTO;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveFondsQuery;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveFonds;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveType;
import com.example.documentmanagementsystem.modules.archive.mapper.ArchiveFondsMapper;
import com.example.documentmanagementsystem.modules.archive.mapper.ArchiveTypeMapper;
import com.example.documentmanagementsystem.modules.archive.service.IArchiveFondsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 档案全宗服务实现
 */
@Slf4j
@Service
public class ArchiveFondsServiceImpl extends ServiceImpl<ArchiveFondsMapper, ArchiveFonds> implements IArchiveFondsService {

    @Resource
    private ArchiveTypeMapper archiveTypeMapper;

    @Override
    public IPage<ArchiveFonds> pageFonds(ArchiveFondsQuery query) {
        LambdaQueryWrapper<ArchiveFonds> wrapper = new LambdaQueryWrapper<>();
        // 按名称模糊查询
        if (StringUtils.hasText(query.getFondsName())) {
            wrapper.like(ArchiveFonds::getFondsName, query.getFondsName());
        }
        // 按全宗号模糊查询
        if (StringUtils.hasText(query.getFondsCode())) {
            wrapper.like(ArchiveFonds::getFondsCode, query.getFondsCode());
        }
        // 按状态筛选
        if (query.getStatus() != null) {
            wrapper.eq(ArchiveFonds::getStatus, query.getStatus());
        }
        // 按排序字段升序（全宗列表按 sort 排列）
        wrapper.orderByAsc(ArchiveFonds::getSort);
        return this.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
    }

    @Override
    public List<ArchiveFonds> listEnabledFonds() {
        return this.list(new LambdaQueryWrapper<ArchiveFonds>()
                .eq(ArchiveFonds::getStatus, 1)
                .orderByAsc(ArchiveFonds::getSort));
    }

    @Override
    public void createFonds(ArchiveFondsDTO dto) {
        // 全宗号唯一性校验
        checkFondsCodeUnique(dto.getFondsCode(), null);
        ArchiveFonds fonds = new ArchiveFonds();
        BeanUtils.copyProperties(dto, fonds);
        if (fonds.getStatus() == null) {
            fonds.setStatus(1);
        }
        this.save(fonds);
        log.info("新增全宗: fondsCode={}, fondsName={}", fonds.getFondsCode(), fonds.getFondsName());
    }

    @Override
    public void updateFonds(ArchiveFondsDTO dto) {
        if (dto.getId() == null) {
            throw new ServiceException("全宗ID不能为空");
        }
        ArchiveFonds exist = this.getById(dto.getId());
        if (exist == null) {
            throw new ServiceException("全宗不存在");
        }
        // 全宗号唯一性校验（排除自身）
        checkFondsCodeUnique(dto.getFondsCode(), dto.getId());
        ArchiveFonds fonds = new ArchiveFonds();
        BeanUtils.copyProperties(dto, fonds);
        this.updateById(fonds);
        log.info("编辑全宗: id={}, fondsCode={}", fonds.getId(), fonds.getFondsCode());
    }

    @Override
    public void deleteFonds(Long id) {
        ArchiveFonds exist = this.getById(id);
        if (exist == null) {
            throw new ServiceException("全宗不存在");
        }
        // 校验是否有门类引用该全宗
        Long typeCount = archiveTypeMapper.selectCount(new LambdaQueryWrapper<ArchiveType>()
                .eq(ArchiveType::getFondsId, id));
        if (typeCount != null && typeCount > 0) {
            throw new ServiceException("该全宗下存在 " + typeCount + " 个门类，无法删除");
        }
        this.removeById(id);
        log.info("删除全宗: id={}, fondsCode={}", id, exist.getFondsCode());
    }

    /**
     * 全宗号唯一性校验（覆盖逻辑删除记录，全宗号不复用）
     */
    private void checkFondsCodeUnique(String fondsCode, Long excludeId) {
        long count = this.baseMapper.countByCodeIncludingDeleted(fondsCode, excludeId);
        if (count > 0) {
            throw new ServiceException("全宗号已存在：" + fondsCode);
        }
    }
}
