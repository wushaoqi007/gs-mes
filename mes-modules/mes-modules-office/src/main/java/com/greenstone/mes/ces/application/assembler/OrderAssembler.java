package com.greenstone.mes.ces.application.assembler;

import com.greenstone.mes.ces.application.dto.cmd.OrderAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.OrderEditCmd;
import com.greenstone.mes.ces.application.dto.event.OrderAddE;
import com.greenstone.mes.ces.application.dto.event.ReceiptAddE;
import com.greenstone.mes.ces.application.dto.result.OrderResult;
import com.greenstone.mes.ces.domain.entity.Order;
import com.greenstone.mes.ces.domain.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-05-24-10:02
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderAssembler {
    Order toOrder(OrderAddCmd addCmd);

    Order toOrder(OrderEditCmd editCmd);

    OrderResult toOrderR(Order order);

    List<OrderResult> toOrderRs(List<Order> orders);

    OrderAddE toOrderAddEvent(Order order);

    @Mapping(target = "purchasedNum", source = "itemNum")
    OrderAddE.Item toOrderItemAddEvent(OrderItem item);

    @Mapping(target = "items", source = "receiptAddEs")
    Order toOrderFromReceiptE(ReceiptAddE eventData, List<ReceiptAddE.Item> receiptAddEs);

    @Mapping(target = "id", source = "orderItemId")
    @Mapping(target = "receivedNum", source = "readyNum")
    OrderItem toOrderItemFromReceiptE(ReceiptAddE.Item receiptAddItemE);
}
