package com.example.documentmanagementsystem.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.documentmanagementsystem.modules.system.entity.SysDict;
import com.example.documentmanagementsystem.modules.system.entity.SysDictItem;

import java.util.List;

/**
 * 系统字典服务
 */
public interface ISysDictService extends IService<SysDict> {

    /**
     * 分页查询字典类型
     */
    IPage<SysDict> pageDicts(String dictName, Integer pageNum, Integer pageSize);

    /**
     * 新增字典类型
     */
    void createDict(SysDict dict);

    /**
     * 编辑字典类型
     */
    void updateDict(SysDict dict);

    /**
     * 删除字典类型（有字典项时禁止删除）
     */
    void deleteDict(Long id);

    /**
     * 按字典编码查询字典项列表（走 Redis 缓存，方案A核心）
     *
     * @param dictCode 字典编码，如 retention_period
     */
    List<SysDictItem> listItemsByCode(String dictCode);

    /**
     * 新增字典项（操作后清除缓存）
     */
    void createItem(SysDictItem item);

    /**
     * 编辑字典项（操作后清除缓存）
     */
    void updateItem(SysDictItem item);

    /**
     * 删除字典项（操作后清除缓存）
     */
    void deleteItem(Long id);
}
