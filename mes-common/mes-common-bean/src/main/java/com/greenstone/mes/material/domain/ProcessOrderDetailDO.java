package com.greenstone.mes.material.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.util.Date;

/**
 * 采购单详情
 *
 * @author wushaoqi
 * @date 2022-05-16-13:00
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@TableName("purchase_order_detail")
public class ProcessOrderDetailDO extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 采购单id
     */
    @TableField("purchase_order_id")
    private Long processOrderId;

    /**
     * 加工单号
     */
    @TableField(exist = false)
    private String worksheetCode;

    /**
     * 项目代码
     */
    @TableField
    private String projectCode;

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

    @TableField
    private Long materialId;

    /**
     * 零件号
     */
    @TableField
    private String code;

    /**
     * 版本
     */
    @TableField
    private String version;

    /**
     * 零件名称
     */
    @TableField
    private String name;

    /**
     * 原始数量
     */
    @TableField
    private Long originalNumber;

    /**
     * 当前数量
     */
    @TableField
    private Long currentNumber;

    /**
     * 图纸数量
     */
    @TableField
    private Integer paperNumber;

    /**
     * 表面处理
     */
    @TableField
    private String surfaceTreatment;

    /**
     * 原材料
     */
    @TableField
    private String rawMaterial;

    /**
     * 质量g
     */
    @TableField
    private String weight;

    /**
     * 打印日期
     */
    @TableField
    private Date printDate;

    /**
     * 已到数
     */
    @TableField
    private Long getNumber;

    /**
     * 进度
     */
    @TableField
    private String status;

    /**
     * 是否变更中
     */
    @TableField
    private String isChanging;

    /**
     * 是否采购
     */
    @TableField
    private String isPurchase;

    /**
     * 是否加急
     */
    @TableField
    private String isFast;

    /**
     * 是否更新件
     */
    @TableField("is_update_parts")
    private Boolean updateParts;

    /**
     * 是否更新件
     */
    @TableField("is_repair_parts")
    private Boolean repairParts;

    /**
     * 加工单位
     */
    @TableField
    private String provider;

    /**
     * 加工纳期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField
    private Date processingTime;

    /**
     * 收货日期
     */
    @TableField
    private Date receivingTime;

    /**
     * 计划纳期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField
    private Date planTime;

    /**
     * 设计
     */
    @TableField
    private String designer;

    /**
     * 类型:加工件、标准件
     */
    @TableField
    private String type;

    /**
     * 比对结果
     */
    @TableField
    private String comparisonResult;

    /**
     * 已扫描数量
     */
    @TableField
    private Integer scanNumber;

    /**
     * 备注
     */
    @TableField
    private String remark;

    /**
     * 变更数量
     */
    @TableField
    private Long applyNumber;

    @TableField
    private String applyReason;

    /**
     * 实际变更数量
     */
    @TableField
    private Long actualChangeNumber;

    /**
     * 购买理由（1：正常新增、2：设计失误、3：需求变更、4：仓库丢失、5：装配丢失，6：其他）
     */
    @TableField("purchase_reason")
    private Integer reason;

    /**
     * 良品数量
     */
    @TableField
    private Integer goodNum;

    /**
     * 入良品库时间
     */
    @TableField
    private Date inGoodStockTime;


}
