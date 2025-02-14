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
 * 采购订单
 *
 * @author wushaoqi
 * @date 2023-05-24-9:43
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    private String id;
    private String serialNo;
    private ProcessStatus status;
    private LocalDate expectReceiveDate;
    private Long purchaserId;
    private String purchaserName;
    private LocalDateTime purchaseDate;
    private String remark;
    private List<OrderItem> items;
}
