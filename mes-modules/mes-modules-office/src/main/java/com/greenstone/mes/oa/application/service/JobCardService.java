package com.greenstone.mes.oa.application.service;

import com.greenstone.mes.oa.dto.cmd.JobCardPrintCmd;
import com.greenstone.mes.system.api.domain.SysFile;

public interface JobCardService {

    SysFile genStationPdf(JobCardPrintCmd printCmd);

    SysFile jobCardPdf(JobCardPrintCmd printCmd);

}
