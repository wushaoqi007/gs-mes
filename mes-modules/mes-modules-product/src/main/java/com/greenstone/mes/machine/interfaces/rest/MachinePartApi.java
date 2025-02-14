package com.greenstone.mes.machine.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.log.annotation.ApiLog;
import com.greenstone.mes.machine.application.dto.cqe.query.MachinePartScanQuery2;
import com.greenstone.mes.machine.application.dto.result.MachinePartScanResp;
import com.greenstone.mes.machine.application.service.MachinePartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/part")
public class MachinePartApi extends BaseController {

    private final MachinePartService partService;

    @ApiLog
    @GetMapping("/scan")
    public AjaxResult partScan(@Validated MachinePartScanQuery2 scanQuery) {
        MachinePartScanResp scanResp = partService.partScan(scanQuery);
        return AjaxResult.success(scanResp);
    }

}
