package com.example.documentmanagementsystem.modules.system.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.documentmanagementsystem.common.exception.ServiceException;
import com.example.documentmanagementsystem.modules.system.entity.SysDict;
import com.example.documentmanagementsystem.modules.system.entity.SysDictItem;
import com.example.documentmanagementsystem.modules.system.mapper.SysDictItemMapper;
import com.example.documentmanagementsystem.modules.system.mapper.SysDictMapper;
import com.example.documentmanagementsystem.modules.system.service.ISysDictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 系统字典服务实现
 *
 * 方案A：字典数据 Redis 缓存
 * - 缓存 Key 设计：dms:dict:{dictCode}
 * - 读：先查缓存 → 未命中查 MySQL → 回填缓存
 * - 写：先改 MySQL → 删除缓存（缓存一致性：删而不是改）
 * - 防护：空值也缓存(防穿透) + TTL 随机抖动(防雪崩)
 */
@Slf4j
@Service
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements ISysDictService {

    /**
     * 缓存 Key 前缀
     */
    private static final String CACHE_KEY_PREFIX = "dms:dict:";

    /**
     * 正常缓存 TTL：30 分钟 + 随机 0~10 分钟（防雪崩：过期时间错开）
     */
    private static final long TTL_MINUTES = 30;

    /**
     * 空值缓存 TTL：5 分钟（防穿透：不存在的编码也缓存空标记）
     */
    private static final long NULL_TTL_MINUTES = 5;

    @Resource
    private SysDictItemMapper dictItemMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public IPage<SysDict> pageDicts(String dictName, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dictName)) {
            wrapper.like(SysDict::getDictName, dictName);
        }
        wrapper.orderByDesc(SysDict::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public void createDict(SysDict dict) {
        // 字典编码唯一性校验
        Long count = this.count(new LambdaQueryWrapper<SysDict>()
                .eq(SysDict::getDictCode, dict.getDictCode()));
        if (count != null && count > 0) {
            throw new ServiceException("字典编码已存在：" + dict.getDictCode());
        }
        if (dict.getStatus() == null) {
            dict.setStatus(1);
        }
        this.save(dict);
        log.info("新增字典: dictCode={}, dictName={}", dict.getDictCode(), dict.getDictName());
    }

    @Override
    public void updateDict(SysDict dict) {
        if (dict.getId() == null) {
            throw new ServiceException("字典ID不能为空");
        }
        this.updateById(dict);
        log.info("编辑字典: id={}", dict.getId());
    }

    @Override
    public void deleteDict(Long id) {
        SysDict exist = this.getById(id);
        if (exist == null) {
            throw new ServiceException("字典不存在");
        }
        // 有字典项时禁止删除
        Long itemCount = dictItemMapper.selectCount(new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getDictId, id));
        if (itemCount != null && itemCount > 0) {
            throw new ServiceException("该字典下存在 " + itemCount + " 个字典项，无法删除");
        }
        this.removeById(id);
        // 删除字典后清理缓存
        evictCache(exist.getDictCode());
        log.info("删除字典: id={}, dictCode={}", id, exist.getDictCode());
    }

    @Override
    public List<SysDictItem> listItemsByCode(String dictCode) {
        // 1. 先查缓存
        String cacheKey = CACHE_KEY_PREFIX + dictCode;
        String cached = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            // 命中缓存（JSON 反序列化），可能是空标记（[]）
            log.debug("字典缓存命中: {}", cacheKey);
            return JSONUtil.toList(cached, SysDictItem.class);
        }

        // 2. 未命中 → 查 MySQL
        log.info("字典缓存未命中，查询 MySQL: {}", cacheKey);
        List<SysDictItem> items = queryItemsFromDb(dictCode);

        // 3. 回填缓存（空值也缓存，防穿透；TTL 随机抖动，防雪崩）
        long ttl = items.isEmpty() ? NULL_TTL_MINUTES
                : TTL_MINUTES + ThreadLocalRandom.current().nextLong(0, 10);
        stringRedisTemplate.opsForValue().set(cacheKey, JSONUtil.toJsonStr(items), ttl, TimeUnit.MINUTES);
        return items;
    }

    @Override
    public void createItem(SysDictItem item) {
        if (item.getDictId() == null) {
            throw new ServiceException("字典ID不能为空");
        }
        if (item.getStatus() == null) {
            item.setStatus(1);
        }
        if (item.getSort() == null) {
            item.setSort(0);
        }
        dictItemMapper.insert(item);
        // 缓存一致性：先改库，后删缓存
        evictCacheByDictId(item.getDictId());
        log.info("新增字典项: dictId={}, itemCode={}", item.getDictId(), item.getItemCode());
    }

    @Override
    public void updateItem(SysDictItem item) {
        if (item.getId() == null) {
            throw new ServiceException("字典项ID不能为空");
        }
        SysDictItem exist = dictItemMapper.selectById(item.getId());
        if (exist == null) {
            throw new ServiceException("字典项不存在");
        }
        dictItemMapper.updateById(item);
        // 缓存一致性：先改库，后删缓存
        evictCacheByDictId(exist.getDictId());
        log.info("编辑字典项: id={}", item.getId());
    }

    @Override
    public void deleteItem(Long id) {
        SysDictItem exist = dictItemMapper.selectById(id);
        if (exist == null) {
            throw new ServiceException("字典项不存在");
        }
        dictItemMapper.deleteById(id);
        // 缓存一致性：先改库，后删缓存
        evictCacheByDictId(exist.getDictId());
        log.info("删除字典项: id={}", id);
    }

    /**
     * 从数据库查询字典项列表
     */
    private List<SysDictItem> queryItemsFromDb(String dictCode) {
        // 根据编码查字典类型
        SysDict dict = this.getOne(new LambdaQueryWrapper<SysDict>()
                .eq(SysDict::getDictCode, dictCode)
                .eq(SysDict::getStatus, 1));
        if (dict == null) {
            // 字典不存在 → 返回空（并缓存空标记，防穿透）
            return Collections.emptyList();
        }
        // 查该字典下的启用字典项
        return dictItemMapper.selectList(new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getDictId, dict.getId())
                .eq(SysDictItem::getStatus, 1)
                .orderByAsc(SysDictItem::getSort));
    }

    /**
     * 按字典ID清除缓存（改字典项时用）
     */
    private void evictCacheByDictId(Long dictId) {
        SysDict dict = this.getById(dictId);
        if (dict != null) {
            evictCache(dict.getDictCode());
        }
    }

    /**
     * 清除指定字典编码的缓存
     */
    private void evictCache(String dictCode) {
        if (dictCode != null) {
            stringRedisTemplate.delete(CACHE_KEY_PREFIX + dictCode);
            log.info("已清除字典缓存: {}", CACHE_KEY_PREFIX + dictCode);
        }
    }
}
