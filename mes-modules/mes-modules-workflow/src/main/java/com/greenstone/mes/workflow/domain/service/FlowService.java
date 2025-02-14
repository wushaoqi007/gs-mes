package com.greenstone.mes.workflow.domain.service;

import com.greenstone.mes.workflow.cmd.FlowCommitCmd;
import com.greenstone.mes.workflow.infrastructure.persistence.FlwHisTaskPo;
import com.greenstone.mes.workflow.infrastructure.persistence.FlwTask;
import com.greenstone.mes.workflow.resp.FlowCommitResp;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;

import java.util.List;

public interface FlowService {

    FlowCommitResp commit(FlowCommitCmd commitCmd);

    void wxApprovalChange(WxCpXmlMessage cpXmlMessage);

    List<FlwTask> todoTasks();

    List<FlwHisTaskPo> hisTask(String instanceNo);
}
