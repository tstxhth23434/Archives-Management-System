package com.example.documentmanagementsystem.modules.archive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.documentmanagementsystem.common.exception.ServiceException;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveFile;
import com.example.documentmanagementsystem.modules.archive.entity.ElectronicFile;
import com.example.documentmanagementsystem.modules.archive.mapper.ArchiveFileMapper;
import com.example.documentmanagementsystem.modules.archive.mapper.ElectronicFileMapper;
import com.example.documentmanagementsystem.modules.archive.service.IElectronicFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * 电子原文服务实现（D12）
 */
@Slf4j
@Service
public class ElectronicFileServiceImpl extends ServiceImpl<ElectronicFileMapper, ElectronicFile> implements IElectronicFileService {

    /** 单个文件大小上限：50MB（与 spring.servlet.multipart.max-file-size 一致） */
    private static final long MAX_FILE_SIZE = 50L * 1024 * 1024;

    private final ArchiveFileMapper archiveFileMapper;

    @Value("${dms.upload-path:${user.home}/dms-uploads/}")
    private String uploadPath;

    @Value("${dms.allowed-extensions:pdf,doc,docx,xls,xlsx,ppt,pptx,txt,jpg,jpeg,png,gif}")
    private String allowedExtensions;

    public ElectronicFileServiceImpl(ArchiveFileMapper archiveFileMapper) {
        this.archiveFileMapper = archiveFileMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ElectronicFile upload(Long archiveId, MultipartFile file) throws IOException {
        if (archiveId == null) {
            throw new ServiceException("档案ID不能为空");
        }
        if (file == null || file.isEmpty()) {
            throw new ServiceException("上传文件不能为空");
        }
        // 档案必须存在
        ArchiveFile archive = archiveFileMapper.selectById(archiveId);
        if (archive == null) {
            throw new ServiceException("档案不存在");
        }
        // 大小限制
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ServiceException("文件大小超过限制（最大 50MB）");
        }
        // 类型白名单校验
        String originalName = file.getOriginalFilename();
        String suffix = "";
        if (StringUtils.hasText(originalName) && originalName.contains(".")) {
            suffix = originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        }
        List<String> allowed = Arrays.asList(allowedExtensions.split(","));
        if (!allowed.contains(suffix)) {
            throw new ServiceException("不支持的文件类型：" + suffix + "（允许：" + allowedExtensions + "）");
        }
        // 存储目录：根/年/月/
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        File dir = new File(uploadPath, dateDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new ServiceException("创建存储目录失败");
        }
        // 文件名：UUID + 后缀（避免中文/重名）
        String storedName = UUID.randomUUID().toString().replace("-", "") + "." + suffix;
        File dest = new File(dir, storedName);
        file.transferTo(dest);

        // 记录库
        ElectronicFile record = new ElectronicFile();
        record.setArchiveId(archiveId);
        record.setFileName(originalName);
        record.setFilePath(dateDir + "/" + storedName);
        record.setFileSize(file.getSize());
        record.setFileType(file.getContentType());
        record.setFileSuffix(suffix);
        record.setIsOriginal(1);
        record.setSort(0);
        record.setUploadTime(java.time.LocalDateTime.now());
        // 记录库（入库失败时清理已写磁盘文件，避免孤儿文件累积）
        try {
            this.save(record);
        } catch (Exception e) {
            if (dest.exists() && !dest.delete()) {
                log.warn("清理上传失败的文件失败: {}", dest.getAbsolutePath());
            }
            throw e;
        }
        log.info("上传电子原文: archiveId={}, fileName={}, size={}", archiveId, originalName, file.getSize());
        return record;
    }

    @Override
    public List<ElectronicFile> listByArchive(Long archiveId) {
        return this.list(new LambdaQueryWrapper<ElectronicFile>()
                .eq(ElectronicFile::getArchiveId, archiveId)
                .orderByAsc(ElectronicFile::getUploadTime));
    }

    @Override
    public File getDiskFile(ElectronicFile record) {
        return new File(uploadPath, record.getFilePath());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteElectronic(Long id) {
        ElectronicFile exist = this.getById(id);
        if (exist == null) {
            throw new ServiceException("电子原文不存在");
        }
        // 先删磁盘文件（失败不影响库删除，仅记日志）
        File disk = getDiskFile(exist);
        if (disk.exists() && !disk.delete()) {
            log.warn("删除磁盘文件失败: {}", disk.getAbsolutePath());
        }
        this.removeById(id);
        log.info("删除电子原文: id={}, fileName={}", id, exist.getFileName());
    }
}
