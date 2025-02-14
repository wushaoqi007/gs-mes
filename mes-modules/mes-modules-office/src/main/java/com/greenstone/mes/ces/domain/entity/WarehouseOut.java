package com.greenstone.mes.ces.domain.entity;

import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 出库单
 *
 * @author wushaoqi
 * @date 2023-065-9:43
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseOut {

    private String id;
    private String serialNo;
    private String warehouseCode;
    private String warehouseName;
    private ProcessStatus status;
    private LocalDate outDate;
    private Long recipientId;
    private String recipientName;
    private Long sponsorId;
    private String sponsorName;
    private LocalDateTime handleDate;
    private String remark;
    private List<WarehouseOutDetail> items;
}
