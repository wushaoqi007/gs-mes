package com.greenstone.mes.ces.application.service;

import com.greenstone.mes.form.dto.cmd.ProcessResult;
import com.greenstone.mes.ces.application.dto.cmd.OrderAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.OrderEditCmd;
import com.greenstone.mes.ces.application.dto.cmd.OrderRemoveCmd;
import com.greenstone.mes.ces.application.dto.event.ReceiptAddE;
import com.greenstone.mes.ces.application.dto.query.OrderFuzzyQuery;
import com.greenstone.mes.ces.application.dto.result.OrderResult;
import com.greenstone.mes.ces.dto.cmd.OrderStatusChangeCmd;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-05-24-14:19
 */
public interface OrderService {
    void add(OrderAddCmd addCmd);

    void edit(OrderEditCmd editCmd);

    void statusChange(OrderStatusChangeCmd statusChangeCmd);

    void remove(OrderRemoveCmd removeCmd);

    List<OrderResult> list(OrderFuzzyQuery query);

    OrderResult detail(String serialNo);

    void receiptAddEvent(ReceiptAddE eventData);

    void approved(ProcessResult processResult);
}
