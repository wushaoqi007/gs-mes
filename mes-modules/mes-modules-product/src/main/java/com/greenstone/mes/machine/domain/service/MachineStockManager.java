package com.greenstone.mes.machine.domain.service;

import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineStockTransferVo;

import javax.validation.Valid;

public interface MachineStockManager {

    void transfer(@Valid MachineStockTransferVo command);

}
