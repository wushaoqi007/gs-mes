package com.greenstone.mes.ces.application.service;

import com.greenstone.mes.form.dto.cmd.ProcessResult;
import com.greenstone.mes.ces.application.dto.cmd.ApplicationRemoveCmd;
import com.greenstone.mes.ces.application.dto.cmd.CesApplicationAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.CesApplicationEditCmd;
import com.greenstone.mes.ces.application.dto.event.OrderAddE;
import com.greenstone.mes.ces.application.dto.event.ReceiptAddE;
import com.greenstone.mes.ces.application.dto.query.ApplicationFuzzyQuery;
import com.greenstone.mes.ces.application.dto.result.CesApplicationResult;
import com.greenstone.mes.ces.application.dto.result.CesApplicationWaitHandleResult;
import com.greenstone.mes.ces.dto.cmd.AppNoticeCmd;
import com.greenstone.mes.ces.dto.cmd.AppStatusChangeCmd;
import com.greenstone.mes.ces.dto.cmd.StateChangeCmd;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/22 8:08
 */

public interface CesApplicationService {

    void add(CesApplicationAddCmd addCmd);

    void edit(CesApplicationEditCmd editCmd);

    void statusChange(AppStatusChangeCmd statusChangeCmd);

    void remove(ApplicationRemoveCmd removeCmd);

    List<CesApplicationResult> list(ApplicationFuzzyQuery query);

    void changeState(StateChangeCmd changeCmd);

    CesApplicationResult detail(String serialNo);

    void orderAddEvent(OrderAddE eventData);

    void receiptAddEvent(ReceiptAddE eventData);

    void notice(AppNoticeCmd noticeCmd);

    List<CesApplicationWaitHandleResult> waitHandle(ApplicationFuzzyQuery query);

    void approved(ProcessResult processResult);
}
