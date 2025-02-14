package com.greenstone.mes.external.application.service;

import com.greenstone.mes.external.dto.cmd.ProcessCmd;
import com.greenstone.mes.external.dto.cmd.ProcessRevokeCmd;
import com.greenstone.mes.external.dto.cmd.ProcessRunCmd;
import com.greenstone.mes.external.dto.cmd.ProcessStartCmd;
import com.greenstone.mes.external.dto.result.ProcInstStartResult;
import com.greenstone.mes.external.dto.result.ProcessRevokeResult;
import com.greenstone.mes.external.dto.result.ProcessRunResult;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/3/2 14:44
 */
public interface ProcessInstanceService {

    ProcessRunResult createAndRun(ProcessStartCmd startCmd);

    ProcInstStartResult createProcess(ProcessStartCmd startCmd);

    List<ProcessRunResult> runProcess(ProcessCmd processCmd);

    ProcessRunResult runProcess(ProcessRunCmd runCmd);

    ProcessRevokeResult revokeProcess(ProcessRevokeCmd revokeCmd);

}
