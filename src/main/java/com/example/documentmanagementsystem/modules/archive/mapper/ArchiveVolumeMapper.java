package com.example.documentmanagementsystem.modules.archive.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveVolume;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 档案案卷 Mapper（D9 只读）
 */
public interface ArchiveVolumeMapper extends BaseMapper<ArchiveVolume> {

    /**
     * 查询某门类下已有的年度列表（用于档案树第三级，倒序）
     */
    @Select("SELECT DISTINCT `year` FROM da_volume WHERE del_flag = 0 AND type_id = #{typeId} AND `year` IS NOT NULL ORDER BY `year` DESC")
    List<Integer> listYearsByType(@Param("typeId") Long typeId);

    /**
     * 查询某 (全宗+门类+年度) 下的最大档号序号（档号规则：全宗号-门类代码-年度-四位序号）
     * 注意：不按 del_flag 过滤（档号不复用，含已删除记录保证序号单调递增，避免撞唯一索引 uk_da_volume_volume_no）
     * 与 ArchiveFileMapper.selectMaxSeq 保持一致
     */
    @Select("SELECT MAX(CAST(SUBSTRING_INDEX(volume_no, '-', -1) AS UNSIGNED)) FROM da_volume " +
            "WHERE fonds_id = #{fondsId} AND type_id = #{typeId} AND `year` = #{year}")
    Integer selectMaxSeq(@Param("fondsId") Long fondsId,
                         @Param("typeId") Long typeId,
                         @Param("year") Integer year);
}
