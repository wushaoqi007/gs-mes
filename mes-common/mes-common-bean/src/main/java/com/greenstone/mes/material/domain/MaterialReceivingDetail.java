package com.greenstone.mes.material.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * 领料单详情表
 *
 * @author wushaoqi
 * @date 2022-08-15-8:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("material_receiving_detail")
public class MaterialReceivingDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;


    /**
     * 领料单ID
     */
    @TableField
    private Long receivingId;

    /**
     * 物料的ID
     */
    @TableField
    private Long materialId;

    /**
     * 物料名称
     */
    @TableField
    private String materialName;

    /**
     * 物料号
     */
    @TableField
    private String materialCode;

    /**
     * 物料版本
     */
    @TableField
    private String materialVersion;

    /**
     * 总数
     */
    @TableField
    private Integer totalNum;

    /**
     * 已领取数量
     */
    @TableField
    private Integer receivedNum;


}
