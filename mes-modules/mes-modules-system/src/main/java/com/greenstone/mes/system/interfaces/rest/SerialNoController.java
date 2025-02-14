package com.greenstone.mes.system.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;
import com.greenstone.mes.system.domain.service.SerialNoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gu_renkai
 * @date 2023/2/6 13:13
 */
@RestController
@RequestMapping("/sn")
public class SerialNoController extends BaseController {

    @Autowired
    private SerialNoService serialNoService;

    @PostMapping("/next")
    public AjaxResult nextSn(@RequestBody SerialNoNextCmd nextCmd) {
        SerialNoR next = serialNoService.getNext(nextCmd);
        return AjaxResult.success(next);
    }

    @PostMapping("/short/next")
    public AjaxResult nextShortSn(@RequestBody SerialNoNextCmd nextCmd) {
        SerialNoR next = serialNoService.getShortNext(nextCmd);
        return AjaxResult.success(next);
    }

    @PostMapping("/next/contract")
    public AjaxResult nextCn(@RequestBody SerialNoNextCmd nextCmd) {
        SerialNoR next = serialNoService.getNextContractNo(nextCmd);
        return AjaxResult.success(next);
    }

}
