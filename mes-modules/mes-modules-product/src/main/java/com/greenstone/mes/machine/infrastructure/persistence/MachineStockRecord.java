package com.greenstone.mes.machine.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * 物料出入库记录对象 material_stock_record
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
@TableName("machine_stock_record")
public class MachineStockRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 操作（不同单据操作不同：如入库、质检等）
     */
    private Integer operation;

    /**
     * 单据编号
     */
    private String serialNo;

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

    /**
     * 备注
     */
    private String remark;

}