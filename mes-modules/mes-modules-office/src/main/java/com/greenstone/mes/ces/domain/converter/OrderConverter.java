package com.greenstone.mes.ces.domain.converter;

import com.greenstone.mes.ces.domain.entity.Order;
import com.greenstone.mes.ces.domain.entity.OrderItem;
import com.greenstone.mes.ces.infrastructure.persistence.OrderDO;
import com.greenstone.mes.ces.infrastructure.persistence.OrderItemDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-05-24-9:58
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderConverter {

    Order toOrder(OrderDO orderDO);

    @Mapping(target = "id", source = "orderDO.id")
    @Mapping(target = "serialNo", source = "orderDO.serialNo")
    @Mapping(target = "status", source = "orderDO.status")
    @Mapping(target = "expectReceiveDate", source = "orderDO.expectReceiveDate")
    @Mapping(target = "remark", source = "orderDO.remark")
    @Mapping(target = "items", source = "itemDOs")
    @Mapping(target = "purchaserId", source = "orderDO.purchaserId")
    @Mapping(target = "purchaserName", source = "orderDO.purchaserName")
    @Mapping(target = "purchaseDate", source = "orderDO.purchaseDate")
    Order toOrder(OrderDO orderDO, List<OrderItemDO> itemDOs);

    OrderDO toOrderDO(Order order);

    OrderItemDO toOrderItemDO(OrderItem item);

    List<OrderItemDO> toOrderItemDOs(List<OrderItem> items);

    OrderItem toOrderItemS(OrderItemDO itemDO);

    List<OrderItem> toOrderItemS(List<OrderItemDO> itemDOs);
}
