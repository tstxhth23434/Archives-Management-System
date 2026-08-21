package com.example.documentmanagementsystem.modules.archive.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.documentmanagementsystem.modules.archive.dto.BorrowDTO;
import com.example.documentmanagementsystem.modules.archive.dto.BorrowVO;
import com.example.documentmanagementsystem.modules.archive.entity.Borrow;

import java.util.List;

/**
 * 借阅单服务接口（D17 申请；D18 审批）
 */
public interface IBorrowService extends IService<Borrow> {

    /**
     * 借阅申请（生成借阅单号，状态待审批）
     */
    void apply(BorrowDTO dto);

    /**
     * 查询当前用户的借阅列表（含档案题名/档号）
     */
    List<BorrowVO> listMine(Long applicantId);

    /**
     * 查询全部借阅单（D18 管理员审批用，含档案题名）
     */
    List<BorrowVO> listAll();
}
