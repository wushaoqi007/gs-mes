package com.greenstone.mes.workflow.domain.helper;

import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.external.enums.TaskStatus;
import com.greenstone.mes.workflow.infrastructure.consts.WxApprovalConst;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;

public class WxApprovalHelper {

    public static ProcessStatus convertProcessStatus(WxCpXmlMessage cpXmlMessage) {
        return switch (cpXmlMessage.getApprovalInfo().getSpStatus()) {
            case WxApprovalConst.SpStatus.WAIT_APPROVE -> ProcessStatus.APPROVING;
            case WxApprovalConst.SpStatus.PASSED -> ProcessStatus.FINISH;
            case WxApprovalConst.SpStatus.REJECTED -> ProcessStatus.REJECTED;
            case WxApprovalConst.SpStatus.REVOKED -> ProcessStatus.REVOKED;
            default -> null;
        };
    }

    public static TaskStatus convertTaskStatus(Integer wxSpStatus) {
        return switch (wxSpStatus) {
            case WxApprovalConst.SpRecord.SpStatus.WAIT_APPROVE -> TaskStatus.PENDING;
            case WxApprovalConst.SpRecord.SpStatus.AGREED -> TaskStatus.APPROVED;
            case WxApprovalConst.SpRecord.SpStatus.REJECTED -> TaskStatus.REJECTED;
            case WxApprovalConst.SpRecord.SpStatus.YZS -> TaskStatus.YZS;
            default -> null;
        };
    }

}
