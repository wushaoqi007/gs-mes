package com.greenstone.mes.external.application.service;

import com.greenstone.mes.external.application.dto.cmd.ProcessSaveCmd;
import com.greenstone.mes.external.application.dto.query.ProcDefQuery;
import com.greenstone.mes.external.application.dto.result.ProcessDefinitionResult;
import com.greenstone.mes.external.application.dto.result.ProcessDefSaveResult;

/**
 * @author gu_renkai
 * @date 2023/2/24 15:52
 */

public interface ProcessDefinitionService {

    ProcessDefinitionResult get(ProcDefQuery procDefQuery);

    ProcessDefSaveResult save(ProcessSaveCmd saveCmd);

    Boolean getDefinitionId(String formId);

}
