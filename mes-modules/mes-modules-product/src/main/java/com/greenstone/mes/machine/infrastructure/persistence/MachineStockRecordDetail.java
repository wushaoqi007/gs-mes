package com.greenstone.mes.machine.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.annotation.Excel;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * 物料出入库记录明细对象 material_stock_record_detail
 *
 * @author gu_renkai
 * @date 2022-02-17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("machine_stock_record_detail")
public class MachineStockRecordDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 记录id
     */
    private Long recordId;

    /**
     * 项目号
     */
    private String projectCode;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 物料ID
     */
    private Long materialId;

    /**
     * 加工单编号
     */
    private String orderSerialNo;

    /**
     * 操作（不同单据操作不同：如入库、质检等）
     */
    private Integer operation;

    /**
     * 出/入库
     */
    private Integer action;

    /**
     * 阶段（仓库的用途，如存放待收件、待质检等零件）
     */
    private Integer stage;

    /**
     * 将操作、动作、阶段组合成行为
     */
    private Integer behavior;

    /**
     * 操作数量
     */
    private Long number;


    /**
     * 经手人
     */
    private String sponsor;

    /**
     * 申请人
     */
    private String applicant;

    /**
     * 申请人工号
     */
    private String applicantNo;

}