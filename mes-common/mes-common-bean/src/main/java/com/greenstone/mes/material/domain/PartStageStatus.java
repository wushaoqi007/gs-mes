
package com.greenstone.mes.material.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.util.Date;

/**
 * 零件阶段进度表
 *
 * @author wushaoqi
 * @date 2022-12-13-13:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("material_part_stage_status")
public class PartStageStatus extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 阶段
     */
    @TableField
    private Integer stage;

    /**
     * 加工单ID
     */
    @TableField
    private Long worksheetId;

    /**
     * 加工单详情ID
     */
    @TableField
    private Long worksheetDetailId;


    /**
     * 项目代码
     */
    @TableField
    private String projectCode;

    /**
     * 加工单号
     */
    @TableField
    private String worksheetCode;

    /**
     * 设备组件的编码
     */
    @TableField
    private String componentCode;

    /**
     * 设备组件的名称
     */
    @TableField
    private String componentName;

    /**
     * 物料ID
     */
    @TableField
    private Long materialId;

    /**
     * 零件号
     */
    @TableField
    private String partCode;

    /**
     * 版本
     */
    @TableField
    private String partVersion;

    /**
     * 入库总量
     */
    @TableField
    private Integer inStockTotal;

    /**
     * 出库总量
     */
    @TableField
    private Integer outStockTotal;

    /**
     * 初次入库时间
     */
    @TableField
    private Date firstInTime;

    /**
     * 最后入库时间
     */
    @TableField
    private Date lastInTime;

    /**
     * 初次出库时间
     */
    @TableField
    private Date firstOutTime;

    /**
     * 最后出库时间
     */
    @TableField
    private Date lastOutTime;

    /**
     * 库存余量
     */
    @TableField
    private Integer stockNum;
}
