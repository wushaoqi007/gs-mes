package com.greenstone.mes.machine.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;

/**
 * @author wushaoqi
 * @date 2023-12-08-9:27
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "machine_receive_detail")
public class MachineReceiveDetailDO extends BaseEntity {
    @Serial
    private static final long serialVersionUID = -2061512478583084097L;

    @TableId(type = IdType.AUTO)
    private String id;
    private String serialNo;
    private Integer operation;
    private String orderSerialNo;
    private String orderDetailId;
    private String requirementSerialNo;
    private String projectCode;
    private Long materialId;
    private String partCode;
    private String partName;
    private String partVersion;
    private Long expectedNumber;
    private Long actualNumber;
    private String warehouseCode;

    private String provider;
}
