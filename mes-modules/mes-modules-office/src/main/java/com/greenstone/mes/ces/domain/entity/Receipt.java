package com.greenstone.mes.ces.domain.entity;

import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * 收货单
 *
 * @author wushaoqi
 * @date 2023-05-25-9:43
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Receipt {

    private String id;
    private String serialNo;
    private ProcessStatus status;
    private LocalDate receiveDate;
    private Long receiveBy;
    private String receiveByName;
    private String remark;
    private List<ReceiptItem> items;
}
