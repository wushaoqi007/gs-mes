package com.greenstone.mes.wxcp.api;

import com.greenstone.mes.common.core.constant.ServiceNameConstants;
import com.greenstone.mes.workflow.cmd.FlowCommitCmd;
import com.greenstone.mes.workflow.resp.FlowCommitResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Repository
@FeignClient(contextId = "remoteWorkflowService", value = ServiceNameConstants.WORKFLOW_SERVICE)
public interface RemoteWorkflowService {

    @PostMapping("/workflow/processes/commit")
    FlowCommitResp commit(@RequestBody FlowCommitCmd commitCmd);

}
