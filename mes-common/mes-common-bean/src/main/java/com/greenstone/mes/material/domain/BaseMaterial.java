package com.greenstone.mes.material.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.common.core.annotation.Excel;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 物料配置对象 base_material
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
@TableName("base_material")
public class BaseMaterial extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 物料编码
     */
    @Excel(name = "物料编码")
    @TableField
    private String code;

    /**
     * 物料名称
     */
    @Excel(name = "物料名称")
    @TableField
    private String name;

    /**
     * 物料版本
     */
    @Excel(name = "物料版本")
    @TableField
    private String version;

    /**
     * 表面处理
     */
    @TableField
    private String surfaceTreatment;

    /**
     * 项目代码
     */
    @Excel(name = "原料")
    @TableField
    private String rawMaterial;

    /**
     * 项目代码
     */
    @Excel(name = "设计设")
    @TableField
    private String designer;

    /**
     * 物料单位
     */
    @Excel(name = "物料单位")
    @TableField
    private String unit;

    /**
     * 重量g
     */
    private String weight;

    /**
     * 物料类型 (1:原料 2:半成品 3:成品)
     */
    @Excel(name = "物料类型 (1:原料 2:半成品 3:成品)")
    @TableField
    private Integer type;

    private Double price;
    private String calculateJson;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime calculateTime;
    private String calculateBy;
    private Long calculateById;
}