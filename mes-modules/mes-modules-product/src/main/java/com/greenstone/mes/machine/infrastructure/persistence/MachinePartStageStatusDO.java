
package com.greenstone.mes.machine.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;
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
@TableName("machine_part_stage_status")
public class MachinePartStageStatusDO extends BaseEntity {


    @Serial
    private static final long serialVersionUID = -7902624080173753480L;
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
    private String orderSerialNo;

    /**
     * 加工单详情ID
     */
    @TableField
    private String orderDetailId;

    /**
     * 项目代码
     */
    @TableField
    private String projectCode;


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
