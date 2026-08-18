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
}
