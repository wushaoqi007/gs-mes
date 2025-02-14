package com.greenstone.mes.market.application.service;

import com.greenstone.mes.ces.dto.cmd.AppStatusChangeCmd;
import com.greenstone.mes.external.dto.result.MailSendResult;
import com.greenstone.mes.market.application.dto.MarketAppSaveCmd;
import com.greenstone.mes.market.application.dto.query.MarketAppFuzzyQuery;
import com.greenstone.mes.market.application.dto.result.MarketAppResult;
import com.greenstone.mes.workflow.mq.ApprovalChangeMsg;

import java.util.List;


public interface MarketAppDataService {

    MarketAppResult detail(String serialNo);

    List<MarketAppResult> list(MarketAppFuzzyQuery fuzzyQuery);

    void delete(List<String> serialNos);

    void changeStatus(AppStatusChangeCmd statusChangeCmd);

    void approval(ApprovalChangeMsg approvalChangeMsg);

    void mailResult(MailSendResult mailSendResult);

    void saveDraft(MarketAppSaveCmd addCmd);

    void saveCommit(MarketAppSaveCmd editCmd);

    MarketAppResult getById(String id);
}
