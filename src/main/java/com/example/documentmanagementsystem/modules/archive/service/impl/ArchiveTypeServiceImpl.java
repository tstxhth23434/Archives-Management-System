package com.example.documentmanagementsystem.modules.archive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.documentmanagementsystem.common.exception.ServiceException;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveTypeDTO;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveTypeQuery;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveType;
import com.example.documentmanagementsystem.modules.archive.mapper.ArchiveTypeMapper;
import com.example.documentmanagementsystem.modules.archive.service.IArchiveTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 档案门类服务实现
 */
@Slf4j
@Service
public class ArchiveTypeServiceImpl extends ServiceImpl<ArchiveTypeMapper, ArchiveType> implements IArchiveTypeService {

    @Override
    public IPage<ArchiveType> pageTypes(ArchiveTypeQuery query) {
        LambdaQueryWrapper<ArchiveType> wrapper = new LambdaQueryWrapper<>();
        // 按名称模糊查询
        if (StringUtils.hasText(query.getTypeName())) {
            wrapper.like(ArchiveType::getTypeName, query.getTypeName());
        }
        // 按门类代码模糊查询
        if (StringUtils.hasText(query.getTypeCode())) {
            wrapper.like(ArchiveType::getTypeCode, query.getTypeCode());
        }
        // 按全宗筛选
        if (query.getFondsId() != null) {
            wrapper.eq(ArchiveType::getFondsId, query.getFondsId());
        }
        // 按状态筛选
        if (query.getStatus() != null) {
            wrapper.eq(ArchiveType::getStatus, query.getStatus());
        }
        wrapper.orderByAsc(ArchiveType::getSort);
        return this.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
    }

    @Override
    public List<ArchiveType> listByFonds(Long fondsId) {
        return this.list(new LambdaQueryWrapper<ArchiveType>()
                .eq(fondsId != null, ArchiveType::getFondsId, fondsId)
                .eq(ArchiveType::getStatus, 1)
                .orderByAsc(ArchiveType::getSort));
    }

    @Override
    public void createType(ArchiveTypeDTO dto) {
        // 同一全宗内门类代码唯一
        checkTypeCodeUnique(dto.getFondsId(), dto.getTypeCode(), null);
        ArchiveType type = new ArchiveType();
        BeanUtils.copyProperties(dto, type);
        if (type.getStatus() == null) {
            type.setStatus(1);
        }
        this.save(type);
        log.info("新增门类: fondsId={}, typeCode={}, typeName={}", type.getFondsId(), type.getTypeCode(), type.getTypeName());
    }

    @Override
    public void updateType(ArchiveTypeDTO dto) {
        if (dto.getId() == null) {
            throw new ServiceException("门类ID不能为空");
        }
        ArchiveType exist = this.getById(dto.getId());
        if (exist == null) {
            throw new ServiceException("门类不存在");
        }
        // 同一全宗内门类代码唯一（排除自身）
        checkTypeCodeUnique(dto.getFondsId(), dto.getTypeCode(), dto.getId());
        ArchiveType type = new ArchiveType();
        BeanUtils.copyProperties(dto, type);
        this.updateById(type);
        log.info("编辑门类: id={}, typeCode={}", type.getId(), type.getTypeCode());
    }

    @Override
    public void deleteType(Long id) {
        ArchiveType exist = this.getById(id);
        if (exist == null) {
            throw new ServiceException("门类不存在");
        }
        // TODO D10：案卷模块完成后，此处补充"门类下存在案卷禁止删除"的引用检查（da_volume）
        this.removeById(id);
        log.info("删除门类: id={}, typeCode={}", id, exist.getTypeCode());
    }

    /**
     * 门类代码唯一性校验（同一全宗内，覆盖逻辑删除记录，门类代码不复用）
     */
    private void checkTypeCodeUnique(Long fondsId, String typeCode, Long excludeId) {
        long count = this.baseMapper.countByTypeCodeIncludingDeleted(fondsId, typeCode, excludeId);
        if (count > 0) {
            throw new ServiceException("该全宗下门类代码已存在：" + typeCode);
        }
    }
}
