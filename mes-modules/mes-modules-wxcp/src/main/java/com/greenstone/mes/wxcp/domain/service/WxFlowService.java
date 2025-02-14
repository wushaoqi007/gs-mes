package com.greenstone.mes.wxcp.domain.service;

import com.greenstone.mes.wxcp.cmd.WxFlowCommitCmd;

public interface WxFlowService {

    String commit(WxFlowCommitCmd commitCmd);

}
