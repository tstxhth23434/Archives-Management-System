package com.example.documentmanagementsystem.modules.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.documentmanagementsystem.common.annotation.OpLog;
import com.example.documentmanagementsystem.common.base.BaseController;
import com.example.documentmanagementsystem.common.result.Result;
import com.example.documentmanagementsystem.modules.system.entity.SysDict;
import com.example.documentmanagementsystem.modules.system.entity.SysDictItem;
import com.example.documentmanagementsystem.modules.system.service.ISysDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 字典管理接口
 * 查询走 Redis 缓存（方案A），管理操作（增删改）后自动清除缓存
 */
@Api(tags = "05-字典管理")
@RestController
@RequestMapping("/api/system/dict")
public class DictController extends BaseController {

    @Resource
    private ISysDictService dictService;

    @ApiOperation("分页查询字典类型")
    @SaCheckPermission("system:dict:query")
    @GetMapping("/page")
    public Result<IPage<SysDict>> page(@RequestParam(required = false) String dictName,
                                       @RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize) {
        return success(dictService.pageDicts(dictName, pageNum, pageSize));
    }

    @ApiOperation("新增字典类型")
    @SaCheckPermission("system:dict:add")
    @OpLog("新增字典")
    @PostMapping
    public Result<Void> add(@RequestBody SysDict dict) {
        dictService.createDict(dict);
        return success("新增成功", null);
    }

    @ApiOperation("编辑字典类型")
    @SaCheckPermission("system:dict:edit")
    @OpLog("编辑字典")
    @PutMapping
    public Result<Void> edit(@RequestBody SysDict dict) {
        dictService.updateDict(dict);
        return success("修改成功", null);
    }

    @ApiOperation("删除字典类型")
    @SaCheckPermission("system:dict:delete")
    @OpLog("删除字典")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dictService.deleteDict(id);
        return success("删除成功", null);
    }

    @ApiOperation("按编码查询字典项列表（Redis 缓存）")
    @GetMapping("/items/{dictCode}")
    public Result<List<SysDictItem>> items(@ApiParam("字典编码，如 retention_period") @PathVariable String dictCode) {
        return success(dictService.listItemsByCode(dictCode));
    }

    @ApiOperation("新增字典项")
    @SaCheckPermission("system:dict:add")
    @OpLog("新增字典项")
    @PostMapping("/item")
    public Result<Void> addItem(@RequestBody SysDictItem item) {
        dictService.createItem(item);
        return success("新增成功", null);
    }

    @ApiOperation("编辑字典项")
    @SaCheckPermission("system:dict:edit")
    @OpLog("编辑字典项")
    @PutMapping("/item")
    public Result<Void> editItem(@RequestBody SysDictItem item) {
        dictService.updateItem(item);
        return success("修改成功", null);
    }

    @ApiOperation("删除字典项")
    @SaCheckPermission("system:dict:delete")
    @DeleteMapping("/item/{id}")
    public Result<Void> deleteItem(@PathVariable Long id) {
        dictService.deleteItem(id);
        return success("删除成功", null);
    }
}
