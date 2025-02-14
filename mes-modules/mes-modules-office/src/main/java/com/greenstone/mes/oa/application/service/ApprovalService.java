package com.greenstone.mes.oa.application.service;

import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.oa.interfaces.request.ApprovalCorrectionImportCommand;
import com.greenstone.mes.oa.interfaces.request.ApprovalExtraWorkImportCommand;
import com.greenstone.mes.oa.interfaces.request.ApprovalNightImportCommand;
import com.greenstone.mes.oa.interfaces.request.ApprovalVacationImportCommand;
import me.chanjar.weixin.cp.bean.oa.WxCpApprovalDetailResult;

import java.util.Date;
import java.util.List;

public interface ApprovalService {

    void sync(CpId cpId, WxCpApprovalDetailResult.WxCpApprovalDetail approvalDetail);

    void importVacations(CpId cpId, List<ApprovalVacationImportCommand> commands);

    void importNights(CpId cpId, List<ApprovalNightImportCommand> commands);

    void importExtraWorks(CpId cpId, List<ApprovalExtraWorkImportCommand> commands);

    void importCorrections(CpId cpId, List<ApprovalCorrectionImportCommand> commands);

    /**
     * 查询所有待审批的单号
     */
    List<String> listApprovalOfAuditing(Date startDate, Date endDate, String cpId);
}
