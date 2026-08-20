package com.example.documentmanagementsystem.modules.archive.service.impl;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.documentmanagementsystem.common.exception.ServiceException;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveFileDTO;
import com.example.documentmanagementsystem.modules.archive.dto.ArchiveFileQuery;
import com.example.documentmanagementsystem.modules.archive.dto.ImportResultVO;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
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

    @Override
    public ImportResultVO importExcel(MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        if (originalName == null || !(originalName.endsWith(".xlsx") || originalName.endsWith(".xls"))) {
            throw new ServiceException("请上传 .xlsx 或 .xls 格式的 Excel 文件");
        }
        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
        List<Map<String, Object>> rows;
        try {
            rows = reader.readAll(); // 第一行作为表头
        } finally {
            reader.close();
        }
        ImportResultVO result = new ImportResultVO();
        result.setTotal(rows.size());
        for (int i = 0; i < rows.size(); i++) {
            int rowNum = i + 2; // 表头第 1 行，数据从第 2 行
            Map<String, Object> row = rows.get(i);
            try {
                importOneRow(row);
                result.setSuccess(result.getSuccess() + 1);
            } catch (Exception e) {
                result.setFail(result.getFail() + 1);
                result.getErrors().add(new ImportResultVO.ErrorItem(rowNum, e.getMessage()));
            }
        }
        log.info("Excel 批量导入: total={}, success={}, fail={}", result.getTotal(), result.getSuccess(), result.getFail());
        return result;
    }

    /**
     * 导入单行（全宗代码/门类代码/题名/责任者/文件日期/年度/保管期限/密级/关键词/页数/摘要）
     * 任意校验失败抛异常，由外层收集为错误行
     */
    private void importOneRow(Map<String, Object> row) {
        String fondsCode = cellStr(row, "全宗代码");
        String typeCode = cellStr(row, "门类代码");
        String title = cellStr(row, "题名");
        String yearStr = cellStr(row, "年度");
        if (!StringUtils.hasText(fondsCode) || !StringUtils.hasText(typeCode)) {
            throw new ServiceException("全宗代码/门类代码不能为空");
        }
        // 全宗存在
        ArchiveFonds fonds = fondsMapper.selectOne(new LambdaQueryWrapper<ArchiveFonds>()
                .eq(ArchiveFonds::getFondsCode, fondsCode));
        if (fonds == null) {
            throw new ServiceException("全宗代码不存在：" + fondsCode);
        }
        // 门类存在且属于该全宗
        ArchiveType type = typeMapper.selectOne(new LambdaQueryWrapper<ArchiveType>()
                .eq(ArchiveType::getFondsId, fonds.getId())
                .eq(ArchiveType::getTypeCode, typeCode));
        if (type == null) {
            throw new ServiceException("门类代码不存在或不属于该全宗：" + typeCode);
        }
        if (!StringUtils.hasText(title)) {
            throw new ServiceException("题名不能为空");
        }
        // 年度必填且为数字
        Integer year = parseYear(yearStr);
        if (year == null) {
            throw new ServiceException("年度不能为空或格式错误：" + yearStr);
        }
        ArchiveFile file = new ArchiveFile();
        file.setFondsId(fonds.getId());
        file.setTypeId(type.getId());
        file.setTitle(title.trim());
        file.setAuthor(cellStr(row, "责任者"));
        file.setDocDate(parseDate(cellStr(row, "文件日期")));
        file.setYear(year);
        file.setRetentionPeriod(nullToEmpty(cellStr(row, "保管期限")));
        file.setSecurityLevel(nullToEmpty(cellStr(row, "密级")));
        file.setKeywords(cellStr(row, "关键词"));
        file.setPages(parseIntSafe(cellStr(row, "页数"), 0));
        file.setSummary(cellStr(row, "摘要"));
        file.setStatus(1);
        // 档号自动生成（序号统计含已删除记录，档号不复用）
        file.setArchiveNo(generateArchiveNo(fonds.getFondsCode(), type.getTypeCode(), fonds.getId(), type.getId(), year));
        this.save(file);
    }

    private String cellStr(Map<String, Object> row, String key) {
        Object v = row.get(key);
        return v == null ? null : String.valueOf(v).trim();
    }

    private String nullToEmpty(String s) {
        return StringUtils.hasText(s) ? s : null;
    }

    private Integer parseYear(String s) {
        if (!StringUtils.hasText(s)) {
            return null;
        }
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseIntSafe(String s, int defaultValue) {
        Integer v = parseYear(s);
        return v == null ? defaultValue : v;
    }

    private LocalDate parseDate(String s) {
        if (!StringUtils.hasText(s)) {
            return null;
        }
        // 支持 2023-01-15 / 2023/01/15 / 20230115
        String v = s.trim().replace("/", "-");
        try {
            if (v.length() == 8) {
                return LocalDate.parse(v, DateTimeFormatter.ofPattern("yyyyMMdd"));
            }
            return LocalDate.parse(v, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            return null;
        }
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
