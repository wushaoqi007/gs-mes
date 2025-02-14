package com.greenstone.mes.reimbursement.application.service;

import com.greenstone.mes.ces.dto.cmd.AppStatusChangeCmd;
import com.greenstone.mes.form.domain.service.BaseFormDataService;
import com.greenstone.mes.reimbursement.application.dto.ReimbursementAppFuzzyQuery;
import com.greenstone.mes.reimbursement.application.dto.result.ReimbursementAppResult;
import com.greenstone.mes.reimbursement.domain.entity.ReimbursementApplication;
import com.greenstone.mes.reimbursement.infrastructure.mapper.ReimbursementApplicationMapper;
import com.greenstone.mes.reimbursement.infrastructure.persistence.ReimbursementAppDO;

import java.util.List;


public interface ReimbursementAppService extends BaseFormDataService<ReimbursementApplication, ReimbursementAppDO, ReimbursementApplicationMapper> {

    ReimbursementAppResult detail(String serialNo);

    List<ReimbursementAppResult> list(ReimbursementAppFuzzyQuery fuzzyQuery);

    void delete(List<String> serialNos);

    void changeStatus(AppStatusChangeCmd statusChangeCmd);

}
