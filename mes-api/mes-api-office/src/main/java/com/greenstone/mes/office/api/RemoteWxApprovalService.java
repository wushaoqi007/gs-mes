package com.greenstone.mes.office.api;


import com.greenstone.mes.common.core.constant.ServiceNameConstants;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.external.workwx.dto.cmd.WxApprovalCheckTakeCommitCmd;
import com.greenstone.mes.external.workwx.dto.cmd.WxApprovalCheckedTakeCommitCmd;
import com.greenstone.mes.external.workwx.dto.cmd.WxApprovalFinishedCommitCmd;
import com.greenstone.mes.office.api.factory.RemoteWxApprovalFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteWxApprovalService", value = ServiceNameConstants.OFFICE_SERVICE, fallbackFactory = RemoteWxApprovalFallbackFactory.class)
public interface RemoteWxApprovalService {

    @PostMapping("/wx/approval/commit/check/take")
    R<String> commitCheckTakeApproval(@RequestBody WxApprovalCheckTakeCommitCmd command);

    @PostMapping("/wx/approval/commit/checked/take")
    R<String> commitCheckedTakeApproval(@RequestBody WxApprovalCheckedTakeCommitCmd command);

    @PostMapping("/wx/approval/commit/finished")
    R<String> commitFinishedApproval(@RequestBody WxApprovalFinishedCommitCmd command);

}
