package com.example.documentmanagementsystem.modules.archive.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.documentmanagementsystem.common.exception.ServiceException;
import com.example.documentmanagementsystem.modules.archive.dto.BorrowDTO;
import com.example.documentmanagementsystem.modules.archive.dto.BorrowVO;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveFile;
import com.example.documentmanagementsystem.modules.archive.entity.Borrow;
import com.example.documentmanagementsystem.modules.archive.mapper.ArchiveFileMapper;
import com.example.documentmanagementsystem.modules.archive.mapper.BorrowMapper;
import com.example.documentmanagementsystem.modules.archive.service.IBorrowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 借阅单服务实现（D17 申请）
 */
@Slf4j
@Service
public class BorrowServiceImpl extends ServiceImpl<BorrowMapper, Borrow> implements IBorrowService {

    @Resource
    private ArchiveFileMapper archiveFileMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void apply(BorrowDTO dto) {
        // 档案必须存在且已归档/封库（整理中不可借）
        ArchiveFile file = archiveFileMapper.selectById(dto.getArchiveId());
        if (file == null) {
            throw new ServiceException("档案不存在");
        }
        if (file.getStatus() == null || file.getStatus() < 2) {
            throw new ServiceException("档案尚未归档，暂不可借阅");
        }
        Borrow borrow = new Borrow();
        borrow.setArchiveId(dto.getArchiveId());
        borrow.setReason(dto.getReason().trim());
        borrow.setStatus(1); // 待审批
        borrow.setApplyTime(LocalDateTime.now());
        // 申请人（Sa-Token 会话）
        try {
            borrow.setApplicantId(StpUtil.getLoginIdAsLong());
            Object uname = StpUtil.getSession().get("username");
            borrow.setApplicantName(uname == null ? null : String.valueOf(uname));
        } catch (Exception ignored) {
            // 无登录上下文时为空
        }
        // 借阅单号：JY-日期-四位序号（当天递增，含已删不复用）
        borrow.setBorrowNo(generateBorrowNo());
        this.save(borrow);
        log.info("借阅申请: borrowNo={}, archiveId={}, applicant={}", borrow.getBorrowNo(), dto.getArchiveId(), borrow.getApplicantName());
    }

    @Override
    public List<BorrowVO> listMine(Long applicantId) {
        List<Borrow> list = this.list(new LambdaQueryWrapper<Borrow>()
                .eq(Borrow::getApplicantId, applicantId)
                .orderByDesc(Borrow::getApplyTime));
        return fillArchiveInfo(list);
    }

    @Override
    public List<BorrowVO> listAll() {
        List<Borrow> list = this.list(new LambdaQueryWrapper<Borrow>()
                .orderByDesc(Borrow::getApplyTime));
        return fillArchiveInfo(list);
    }

    /**
     * 填充档案题名/档号
     */
    private List<BorrowVO> fillArchiveInfo(List<Borrow> list) {
        List<BorrowVO> result = new ArrayList<>();
        for (Borrow b : list) {
            BorrowVO vo = new BorrowVO();
            BeanUtils.copyProperties(b, vo);
            ArchiveFile file = archiveFileMapper.selectById(b.getArchiveId());
            if (file != null) {
                vo.setArchiveTitle(file.getTitle());
                vo.setArchiveNo(file.getArchiveNo());
            }
            result.add(vo);
        }
        return result;
    }

    /**
     * 借阅单号：JY-20260820-0001（当天最大序号+1，序号统计含已删记录不复用）
     */
    private String generateBorrowNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "JY-" + date + "-";
        // 查当天最大序号（含已删，防止唯一索引冲突）
        Long maxId = this.baseMapper.selectCount(new LambdaQueryWrapper<Borrow>()
                .likeRight(Borrow::getBorrowNo, prefix)) + 1;
        return prefix + String.format("%04d", maxId);
    }
}
