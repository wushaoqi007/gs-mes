package com.greenstone.mes.ces.application.service;

import com.greenstone.mes.form.dto.cmd.ProcessResult;
import com.greenstone.mes.ces.application.dto.cmd.ReceiptAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.ReceiptEditCmd;
import com.greenstone.mes.ces.application.dto.cmd.ReceiptRemoveCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseInAddCmd;
import com.greenstone.mes.ces.application.dto.query.ReceiptFuzzyQuery;
import com.greenstone.mes.ces.application.dto.result.ReceiptResult;
import com.greenstone.mes.ces.dto.cmd.ReceiptStatusChangeCmd;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-05-25-14:19
 */
public interface ReceiptService {
    void add(ReceiptAddCmd addCmd);

    void edit(ReceiptEditCmd editCmd);

    void statusChange(ReceiptStatusChangeCmd statusChangeCmd);

    void remove(ReceiptRemoveCmd removeCmd);

    List<ReceiptResult> list(ReceiptFuzzyQuery query);

    ReceiptResult detail(String serialNo);

    List<WarehouseInAddCmd> createWarehouseIn(String serialNo);

    void approved(ProcessResult processResult);
}
