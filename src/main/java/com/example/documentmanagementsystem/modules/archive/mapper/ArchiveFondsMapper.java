package com.example.documentmanagementsystem.modules.archive.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveFonds;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 档案全宗 Mapper
 */
public interface ArchiveFondsMapper extends BaseMapper<ArchiveFonds> {

    /**
     * 按全宗号统计记录数（包含逻辑删除的记录）
     * 逻辑删除后记录仍在表中（del_flag=1），若唯一校验只查 del_flag=0，
     * 重建同全宗号会与唯一索引 uk_da_fonds_fonds_code 冲突抛 DuplicateKeyException。
     * 业务约定：全宗号不复用，因此校验必须覆盖已删除记录。
     */
    @Select("<script>SELECT COUNT(*) FROM da_fonds WHERE fonds_code = #{fondsCode}" +
            "<if test='excludeId != null'> AND id != #{excludeId}</if></script>")
    long countByCodeIncludingDeleted(@Param("fondsCode") String fondsCode, @Param("excludeId") Long excludeId);
}
