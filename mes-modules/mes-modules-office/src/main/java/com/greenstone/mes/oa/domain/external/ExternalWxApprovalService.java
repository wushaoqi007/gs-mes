package com.greenstone.mes.oa.domain.external;

import com.greenstone.mes.external.workwx.dto.cmd.WxApprovalCheckTakeCommitCmd;
import com.greenstone.mes.external.workwx.dto.cmd.WxApprovalCheckedTakeCommitCmd;
import com.greenstone.mes.external.workwx.dto.cmd.WxApprovalFinishedCommitCmd;

public interface ExternalWxApprovalService {

    /**
     * 向企业微信提交质检取件审批申请
     */
    String commitCheckTakeApproval(WxApprovalCheckTakeCommitCmd command);

    String commitCheckedTakeApproval(WxApprovalCheckedTakeCommitCmd command);

    String commitFinishedApproval(WxApprovalFinishedCommitCmd command);
}
