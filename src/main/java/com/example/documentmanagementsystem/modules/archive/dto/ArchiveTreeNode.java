package com.example.documentmanagementsystem.modules.archive.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 档案树节点（全宗→门类→年度 三级）
 */
@Data
@ApiModel("档案树节点")
public class ArchiveTreeNode {

    @ApiModelProperty("节点ID（全宗/门类为业务ID，年度为年份值）")
    private Long id;

    /**
     * 全局唯一节点键（el-tree node-key 用，如 fonds-1 / type-1 / year-1-2024）
     * 年度节点 id 只是年份值，不同门类下会重复，必须用组合键避免树节点 map 覆盖
     */
    @ApiModelProperty("全局唯一节点键")
    private String nodeKey;

    @ApiModelProperty("节点名称")
    private String label;

    /**
     * 节点类型：fonds-全宗 type-门类 year-年度
     */
    @ApiModelProperty("节点类型：fonds/type/year")
    private String type;

    @ApiModelProperty("子节点")
    private List<ArchiveTreeNode> children = new ArrayList<>();
}
