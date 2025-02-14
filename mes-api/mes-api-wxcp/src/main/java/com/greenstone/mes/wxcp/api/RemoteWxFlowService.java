package com.greenstone.mes.wxcp.api;

import com.greenstone.mes.common.core.constant.ServiceNameConstants;
import com.greenstone.mes.workflow.resp.FlowCommitResp;
import com.greenstone.mes.wxcp.cmd.WxFlowCommitCmd;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Repository
@FeignClient(contextId = "remoteWxFlowService", value = ServiceNameConstants.WXCP_SERVICE)
public interface RemoteWxFlowService {

    @PostMapping("/wxcp/flow/commit")
    FlowCommitResp commit(@RequestBody WxFlowCommitCmd commitCmd);

}
