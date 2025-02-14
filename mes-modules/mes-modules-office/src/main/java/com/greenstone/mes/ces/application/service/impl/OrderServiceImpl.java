package com.greenstone.mes.ces.application.service.impl;

import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.ces.application.assembler.OrderAssembler;
import com.greenstone.mes.ces.application.dto.cmd.OrderAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.OrderEditCmd;
import com.greenstone.mes.ces.application.dto.cmd.OrderRemoveCmd;
import com.greenstone.mes.ces.application.dto.event.ReceiptAddE;
import com.greenstone.mes.ces.application.dto.query.OrderFuzzyQuery;
import com.greenstone.mes.ces.application.dto.result.OrderResult;
import com.greenstone.mes.ces.application.event.OrderAddEvent;
import com.greenstone.mes.ces.application.service.OrderService;
import com.greenstone.mes.ces.domain.entity.Order;
import com.greenstone.mes.ces.domain.entity.OrderItem;
import com.greenstone.mes.ces.domain.repository.ItemSpecRepository;
import com.greenstone.mes.ces.domain.repository.OrderRepository;
import com.greenstone.mes.ces.dto.cmd.OrderStatusChangeCmd;
import com.greenstone.mes.common.core.enums.OrderError;
import com.greenstone.mes.common.core.enums.FormError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.external.application.service.ProcessInstanceService;
import com.greenstone.mes.external.dto.cmd.ProcessStartCmd;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.form.dto.cmd.ProcessResult;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-05-24-14:19
 */
@Slf4j
@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderAssembler assembler;
    private final RemoteSystemService systemService;
    private final ProcessInstanceService flowService;
    private final ApplicationEventPublisher eventPublisher;
    private final ItemSpecRepository itemSpecRepository;

    @Override
    public List<OrderResult> list(OrderFuzzyQuery query) {
        log.info("OrderFuzzyQuery params:{}", query);
        List<Order> orders = orderRepository.list(query);
        return assembler.toOrderRs(orders);
    }

    @Override
    public OrderResult detail(String serialNo) {
        log.info("detail params:{}", serialNo);
        Order order = orderRepository.detail(serialNo);
        return assembler.toOrderR(order);
    }

    @Override
    public void receiptAddEvent(ReceiptAddE eventData) {
        log.info("ReceiptAddE params:{}", eventData);
        orderRepository.updateNum(assembler.toOrderFromReceiptE(eventData, eventData.getItems()));
    }

    @Override
    public void approved(ProcessResult processResult) {
        Order changeCmd = Order.builder().serialNo(processResult.getSerialNo())
                .status(ProcessStatus.APPROVED).build();
        orderRepository.changeStatus(changeCmd);
    }

    @Override
    public void add(OrderAddCmd addCmd) {
        log.info("OrderAddCmd params:{}", addCmd);
        Order order = assembler.toOrder(addCmd);
        SerialNoNextCmd nextCmd =
                SerialNoNextCmd.builder().type("purchase_order_manage").prefix("POM" + DateUtil.dateSerialStrNow()).build();
        SerialNoR serialNoR = systemService.getNextSn(nextCmd);

        order.setSerialNo(serialNoR.getSerialNo());
        order.setStatus(addCmd.isCommit() ? ProcessStatus.APPROVING : ProcessStatus.DRAFT);
        order.setPurchaserId(SecurityUtils.getLoginUser().getUser().getUserId());
        order.setPurchaserName(SecurityUtils.getLoginUser().getUser().getNickName());
        // 校验：去除自定义物品，只能选物品档案中物品
        for (OrderItem item : order.getItems()) {
            if (!itemSpecRepository.existByItemCode(item.getItemCode())) {
                throw new ServiceException(FormError.E70105);
            }
        }
        log.info("add Order params:{}", order);
        orderRepository.add(order);

        if (addCmd.isCommit()) {
            ProcessStartCmd startCmd = ProcessStartCmd.builder().formId("purchase_order").serialNo(serialNoR.getSerialNo()).build();
            log.info("commit ProcessStartCmd params:{}", startCmd);
            flowService.createAndRun(startCmd);
        }
    }

    @Transactional
    @Override
    public void edit(OrderEditCmd editCmd) {
        log.info("OrderEditCmd params:{}", editCmd);
        Order orderFound = orderRepository.get(editCmd.getSerialNo());
        if (orderFound == null) {
            throw new ServiceException(OrderError.E90101);
        }
        if (orderFound.getStatus() != ProcessStatus.DRAFT) {
            throw new ServiceException(OrderError.E90102);
        }

        if (editCmd.isCommit()) {
            ProcessStartCmd startCmd = ProcessStartCmd.builder().formId("purchase_order").serialNo(editCmd.getSerialNo()).build();
            flowService.createAndRun(startCmd);
        }
        Order order = assembler.toOrder(editCmd);
        order.setStatus(editCmd.isCommit() ? ProcessStatus.COMMITTED : ProcessStatus.DRAFT);
        for (OrderItem item : order.getItems()) {
            item.setSerialNo(editCmd.getSerialNo());
            if (!itemSpecRepository.existByItemCode(item.getItemCode())) {
                throw new ServiceException(FormError.E70105);
            }
        }
        log.info("edit Order params:{}", order);
        orderRepository.edit(order);
    }

    @Override
    public void statusChange(OrderStatusChangeCmd statusChangeCmd) {
        log.info("OrderStatusChangeCmd params:{}", statusChangeCmd);
        if (statusChangeCmd.getStatus() == ProcessStatus.COMMITTED) {
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                Order orderFound = orderRepository.get(serialNo);
                if (orderFound == null) {
                    throw new ServiceException(OrderError.E90101);
                }
                if (orderFound.getStatus() != ProcessStatus.DRAFT) {
                    throw new ServiceException(OrderError.E90102);
                }
                orderRepository.changeStatus(Order.builder().status(statusChangeCmd.getStatus()).serialNo(serialNo).build());
                ProcessStartCmd startCmd = ProcessStartCmd.builder().formId("purchase_order").serialNo(serialNo).build();
                flowService.createAndRun(startCmd);
            }
        } else if (statusChangeCmd.getStatus() == ProcessStatus.CLOSED) {
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                Order orderFound = orderRepository.get(serialNo);
                if (orderFound == null) {
                    throw new ServiceException(OrderError.E90101);
                }
                if (orderFound.getStatus() != ProcessStatus.APPROVED) {
                    throw new ServiceException(OrderError.E90103);
                }
            }
            orderRepository.statusChange(statusChangeCmd);
        } else if (statusChangeCmd.getStatus() == ProcessStatus.APPROVED) {
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                Order orderFound = orderRepository.detail(serialNo);
                if (orderFound == null) {
                    throw new ServiceException(OrderError.E90101);
                }
                orderRepository.changeStatus(Order.builder().status(statusChangeCmd.getStatus()).serialNo(serialNo).build());
                // 防止多次点击
                if (orderFound.getStatus() != ProcessStatus.APPROVED) {
                    eventPublisher.publishEvent(new OrderAddEvent(assembler.toOrderAddEvent(orderFound)));
                }
            }
        } else {
            orderRepository.statusChange(statusChangeCmd);
        }
    }

    @Transactional
    @Override
    public void remove(OrderRemoveCmd removeCmd) {
        log.info("OrderStatusChangeCmd params:{}", removeCmd);
        orderRepository.remove(removeCmd.getSerialNos());
    }

}
