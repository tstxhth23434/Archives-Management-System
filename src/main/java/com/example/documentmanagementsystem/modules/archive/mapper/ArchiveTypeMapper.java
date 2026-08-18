package com.example.documentmanagementsystem.modules.archive.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.documentmanagementsystem.modules.archive.entity.ArchiveType;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 档案门类 Mapper
 */
public interface ArchiveTypeMapper extends BaseMapper<ArchiveType> {

    /**
     * 按 (全宗ID, 门类代码) 统计记录数（包含逻辑删除的记录）
     * 逻辑删除后记录仍在表中（del_flag=1），若唯一校验只查 del_flag=0，
     * 重建同门类代码会与唯一索引 uk_da_archive_type_code_fonds 冲突抛 DuplicateKeyException。
     * 业务约定：同一全宗下门类代码不复用，因此校验必须覆盖已删除记录。
     */
    @Select("<script>SELECT COUNT(*) FROM da_archive_type WHERE fonds_id = #{fondsId} AND type_code = #{typeCode}" +
            "<if test='excludeId != null'> AND id != #{excludeId}</if></script>")
    long countByTypeCodeIncludingDeleted(@Param("fondsId") Long fondsId,
                                         @Param("typeCode") String typeCode,
                                         @Param("excludeId") Long excludeId);
}
