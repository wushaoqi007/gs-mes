package com.greenstone.mes.machine.application.service;

import com.greenstone.mes.machine.application.dto.cqe.query.MachinePartScanQuery2;
import com.greenstone.mes.machine.application.dto.result.MachinePartScanResp;

public interface MachinePartService {

    MachinePartScanResp partScan(MachinePartScanQuery2 scanQuery);

}
