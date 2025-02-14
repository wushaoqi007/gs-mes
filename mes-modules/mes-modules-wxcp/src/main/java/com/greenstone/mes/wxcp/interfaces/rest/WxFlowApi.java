package com.greenstone.mes.wxcp.interfaces.rest;

import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.log.annotation.ApiLog;
import com.greenstone.mes.common.security.annotation.InnerAuth;
import com.greenstone.mes.workflow.resp.FlowCommitResp;
import com.greenstone.mes.wxcp.cmd.WxFlowCommitCmd;
import com.greenstone.mes.wxcp.domain.service.WxFlowService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/wxcp/flow")
public class WxFlowApi {

    private final WxFlowService wxFlowService;

    @ApiLog
    @PostMapping("/commit")
    public AjaxResult commit(@RequestBody @Validated WxFlowCommitCmd commitCmd) {
        String spNo = wxFlowService.commit(commitCmd);
        return AjaxResult.success(FlowCommitResp.builder().instanceNo(spNo).build());
    }

}
