package com.greenstone.mes.material.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.annotation.Excel;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * 仓库配置对象 base_warehouse
 *
 * @author gu_renkai
 * @date 2022-01-21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("base_warehouse")
public class BaseWarehouse extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 仓库编码
     */
    @Excel(name = "仓库编码")
    @TableField
    private String code;

    /**
     * 仓库名称
     */
    @Excel(name = "仓库名称")
    @TableField
    private String name;

    @TableField
    private String projectCode;

    /**
     * 库位（parent_id对应的仓库名称）
     */
    @TableField(exist = false)
    private String location;

    /**
     * 阶段
     */
    @Excel(name = "阶段")
    @TableField
    private Integer stage;

    @TableField(exist = false)
    private Integer[] stages;

    /**
     * 仓库地址
     */
    @Excel(name = "仓库地址")
    @TableField
    private String address;

    /**
     * 上一级id
     */
    @TableField
    private Long parentId;

    @TableField
    private String remark;

    /**
     * 仓库类型0：原始仓库；1:砧板
     */
    @TableField
    private Integer type;

}