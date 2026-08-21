package com.example.documentmanagementsystem.modules.archive.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.example.documentmanagementsystem.common.annotation.OpLog;
import com.example.documentmanagementsystem.common.base.BaseController;
import com.example.documentmanagementsystem.common.result.Result;
import com.example.documentmanagementsystem.modules.archive.dto.BorrowDTO;
import com.example.documentmanagementsystem.modules.archive.dto.BorrowVO;
import com.example.documentmanagementsystem.modules.archive.service.IBorrowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 借阅接口（D17 申请；D18 审批/归还）
 */
@Api(tags = "15-借阅管理")
@RestController
@RequestMapping("/api/archive/borrow")
public class BorrowController extends BaseController {

    @Resource
    private IBorrowService borrowService;

    @ApiOperation("借阅申请（生成借阅单，待审批）")
    @SaCheckPermission("archive:borrow:apply")
    @OpLog("借阅申请")
    @PostMapping
    public Result<Void> apply(@Validated @RequestBody BorrowDTO dto) {
        borrowService.apply(dto);
        return success("申请成功，等待审批", null);
    }

    @ApiOperation("我的借阅列表")
    @SaCheckPermission("archive:borrow:apply")
    @GetMapping("/mine")
    public Result<List<BorrowVO>> mine() {
        return success(borrowService.listMine(StpUtil.getLoginIdAsLong()));
    }

    @ApiOperation("全部借阅单（D18 管理员审批）")
    @SaCheckPermission("archive:borrow:approve")
    @GetMapping("/list")
    public Result<List<BorrowVO>> list() {
        return success(borrowService.listAll());
    }
}
