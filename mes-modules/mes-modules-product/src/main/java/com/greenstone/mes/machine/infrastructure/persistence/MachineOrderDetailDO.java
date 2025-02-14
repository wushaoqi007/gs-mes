package com.greenstone.mes.machine.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author wushaoqi
 * @date 2023-11-23-15:19
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "machine_order_detail")
public class MachineOrderDetailDO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String serialNo;
    private String inquiryPriceSerialNo;
    private String requirementSerialNo;
    private String projectCode;
    private String hierarchy;
    private Long materialId;
    private String partCode;
    private String partName;
    private String partVersion;
    private Long processNumber;

    private LocalDate processDeadline;
    private LocalDate planDeadline;
    private String provider;

    private String surfaceTreatment;
    private String rawMaterial;
    private String weight;
    private String designer;
    private String remark;

    private LocalDateTime receiveTime;
    private Long receivedNumber;
    private LocalDateTime inStockTime;
    private Long inStockNumber;

    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    private Long createBy;
    private LocalDateTime createTime;
    private Long updateBy;
    private LocalDateTime updateTime;

    private String attachmentName;
    private String attachmentPath;
}
