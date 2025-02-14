package com.greenstone.mes.ces.domain.entity;

import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-11-08-13:07
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Requisition {
    private String id;
    private String serialNo;
    private ProcessStatus status;
    private LocalDateTime requisitionDate;
    private Long requisitionerId;
    private String requisitionerName;
    private String requisitionerNo;
    private String remark;
    private List<RequisitionItem> items;
}
