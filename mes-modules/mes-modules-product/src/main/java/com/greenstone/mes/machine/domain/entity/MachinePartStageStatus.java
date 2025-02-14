
package com.greenstone.mes.machine.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachinePartStageStatus {
    private Long id;
    private Integer stage;
    private String orderSerialNo;
    private String orderDetailId;
    private String projectCode;
    private Long materialId;
    private String partCode;
    private String partVersion;
    private Integer inStockTotal;
    private Integer outStockTotal;
    private Date firstInTime;
    private Date lastInTime;
    private Date firstOutTime;
    private Date lastOutTime;
    private Integer stockNum;
}
