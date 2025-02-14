package com.greenstone.mes.machine.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import com.greenstone.mes.machine.infrastructure.enums.CheckResultType;
import com.greenstone.mes.material.infrastructure.enums.CheckResult;
import lombok.*;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author wushaoqi
 * @date 2023-12-11-11:27
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "machine_check_detail")
public class MachineCheckDetailDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1818073029003838811L;

    @TableId(type = IdType.AUTO)
    private String id;
    private String serialNo;
    private String orderSerialNo;
    private String orderDetailId;
    private String requirementSerialNo;
    private String projectCode;
    private Long materialId;
    private String partCode;
    private String partName;
    private String partVersion;
    private String designer;
    private Long takeNumber;
    private Long checkedNumber;
    private Long toBeCheckedNumber;
    private LocalDateTime receiveTime;
    private Long receivedNumber;
    private String outWarehouseCode;
    private CheckResultType checkResultType;
    private CheckResult checkResult;
    private String ngType;
    private String subNgType;
    private String inWarehouseCode;
    private LocalDate checkDate;
    private String checkBy;
    private Long checkById;
    private String checkByNo;
    private String provider;
}
