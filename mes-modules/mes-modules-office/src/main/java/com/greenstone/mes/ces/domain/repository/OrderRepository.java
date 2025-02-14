package com.greenstone.mes.ces.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.ces.application.dto.query.OrderFuzzyQuery;
import com.greenstone.mes.ces.domain.converter.OrderConverter;
import com.greenstone.mes.ces.domain.entity.Order;
import com.greenstone.mes.ces.domain.entity.OrderItem;
import com.greenstone.mes.ces.dto.cmd.OrderStatusChangeCmd;
import com.greenstone.mes.ces.infrastructure.mapper.OrderItemMapper;
import com.greenstone.mes.ces.infrastructure.mapper.OrderMapper;
import com.greenstone.mes.ces.infrastructure.persistence.OrderDO;
import com.greenstone.mes.ces.infrastructure.persistence.OrderItemDO;
import com.greenstone.mes.common.core.enums.OrderError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author wushaoqi
 * @date 2023-05-24-10:04
 */
@Service
@AllArgsConstructor
public class OrderRepository {
    private final OrderMapper orderMapper;
    private final OrderItemMapper itemMapper;
    private final OrderConverter converter;
    private final RemoteSystemService systemService;


    public Order get(String serialNo) {
        return converter.toOrder(orderMapper.getOneOnly(OrderDO.builder().serialNo(serialNo).build()));
    }

    public void statusChange(OrderStatusChangeCmd statusChangeCmd) {
        LambdaUpdateWrapper<OrderDO> updateWrapper = Wrappers.lambdaUpdate(OrderDO.class).set(OrderDO::getStatus, statusChangeCmd.getStatus())
                .in(OrderDO::getSerialNo, statusChangeCmd.getSerialNos());
        orderMapper.update(updateWrapper);
    }

    public void changeStatus(Order order) {
        LambdaUpdateWrapper<OrderDO> updateWrapper = Wrappers.lambdaUpdate(OrderDO.class)
                .eq(OrderDO::getSerialNo, order.getSerialNo())
                .set(OrderDO::getStatus, order.getStatus());
        orderMapper.update(updateWrapper);
    }

    public Order detail(String serialNo) {
        OrderDO select = OrderDO.builder().serialNo(serialNo).build();
        OrderDO orderDO = orderMapper.getOneOnly(select);
        if (orderDO == null) {
            throw new ServiceException("选择的采购订单不存在，请重新选择");
        }
        List<OrderItemDO> itemDOS = itemMapper.list(OrderItemDO.builder().serialNo(serialNo).build());
        return converter.toOrder(orderDO, itemDOS);
    }

    public List<Order> list(OrderFuzzyQuery fuzzyQuery) {
        QueryWrapper<OrderDO> fuzzyQueryWrapper = new QueryWrapper<>();
        fuzzyQueryWrapper.orderByDesc("create_time");
        if (StrUtil.isNotEmpty(fuzzyQuery.getKey()) && CollUtil.isNotEmpty(fuzzyQuery.getFields())) {
            fuzzyQueryWrapper.and(wrapper -> {
                for (String field : fuzzyQuery.getFields()) {
                    String fieldCode = StrUtil.toSymbolCase(field, '_');
                    wrapper.or().like(fieldCode, fuzzyQuery.getKey());
                }
            });
        }
        if (Objects.nonNull(fuzzyQuery.getState())) {
            fuzzyQueryWrapper.eq("state", fuzzyQuery.getState());
        }
        List<Order> orders = new ArrayList<>();
        List<OrderDO> orderDOS = orderMapper.selectList(fuzzyQueryWrapper);
        for (OrderDO orderDO : orderDOS) {
            List<OrderItemDO> itemDOS = itemMapper.list(OrderItemDO.builder().serialNo(orderDO.getSerialNo()).build());
            orders.add(converter.toOrder(orderDO, itemDOS));
        }
        return orders;
    }

    public void add(Order order) {
        OrderDO orderDO = converter.toOrderDO(order);
        List<OrderItemDO> itemDOS = converter.toOrderItemDOs(order.getItems());
        orderMapper.insert(orderDO);
        for (OrderItemDO itemDO : itemDOS) {
            itemDO.setSerialNo(orderDO.getSerialNo());
        }
        itemMapper.insertBatchSomeColumn(itemDOS);
    }

    public void edit(Order order) {
        OrderDO orderDO = converter.toOrderDO(order);
        List<OrderItemDO> itemDOS = converter.toOrderItemDOs(order.getItems());

        orderMapper.update(orderDO, Wrappers.lambdaQuery(OrderDO.class).eq(OrderDO::getSerialNo, orderDO.getSerialNo()));
        itemMapper.delete(OrderItemDO.builder().serialNo(orderDO.getSerialNo()).build());
        itemMapper.insertBatchSomeColumn(itemDOS);
    }

    public void remove(List<String> serialNos) {
        for (String serialNo : serialNos) {
            OrderDO appFound = orderMapper.getOneOnly(OrderDO.builder().serialNo(serialNo).build());
            if (appFound == null) {
                throw new ServiceException(OrderError.E90101);
            }
            if (appFound.getStatus() != ProcessStatus.DRAFT) {
                throw new ServiceException(OrderError.E90102);
            }
        }

        LambdaQueryWrapper<OrderDO> appWrapper = Wrappers.lambdaQuery(OrderDO.class).in(OrderDO::getSerialNo, serialNos);
        orderMapper.delete(appWrapper);
        LambdaQueryWrapper<OrderItemDO> itemWrapper = Wrappers.lambdaQuery(OrderItemDO.class).in(OrderItemDO::getSerialNo,
                serialNos);
        itemMapper.delete(itemWrapper);
    }

    public void updateNum(Order order) {
        for (OrderItem updateItem : order.getItems()) {
            if (Objects.isNull(updateItem.getId())) {
                continue;
            }
            OrderItemDO findItemDO = itemMapper.getOneOnly(OrderItemDO.builder().id(updateItem.getId()).build());
            if (Objects.nonNull(findItemDO) && Objects.nonNull(updateItem.getReceivedNum())) {
                findItemDO.setReceivedNum(findItemDO.getReceivedNum() == null ? updateItem.getReceivedNum() : updateItem.getReceivedNum() + findItemDO.getReceivedNum());
                itemMapper.updateById(findItemDO);
            }
        }
    }
}
