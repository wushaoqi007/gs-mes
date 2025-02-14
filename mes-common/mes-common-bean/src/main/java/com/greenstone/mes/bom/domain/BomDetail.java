package com.greenstone.mes.bom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.annotation.Excel;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * BOM明细对象 bom_detail
 *
 * @author gu_renkai
 * @date 2022-01-25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("bom_detail")
public class BomDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * BOM ID
     */
    @TableField
    private Long bomId;

    /**
     * 物料ID
     */
    @TableField
    private Long materialId;

    /**
     * 物料数量
     */
    @TableField
    private Long materialNumber;

}