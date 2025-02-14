package com.greenstone.mes.oa.interfaces.rest;

import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.oa.application.service.JobCardService;
import com.greenstone.mes.oa.dto.cmd.JobCardPrintCmd;
import com.greenstone.mes.system.api.domain.SysFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping
public class JobCardApi {

    private final JobCardService jobCardService;

    @PostMapping("/jobCard/print")
    public AjaxResult print(@RequestBody JobCardPrintCmd printCmd) {
        SysFile file = jobCardService.jobCardPdf(printCmd);
        return AjaxResult.success(file);
    }

    @PostMapping("/stationCard/print")
    public AjaxResult printAll(@RequestBody JobCardPrintCmd printCmd) {
        SysFile file = jobCardService.genStationPdf(printCmd);
        return AjaxResult.success(file);
    }

}
