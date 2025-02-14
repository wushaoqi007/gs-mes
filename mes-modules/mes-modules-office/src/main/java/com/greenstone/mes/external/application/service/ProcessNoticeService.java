package com.greenstone.mes.external.application.service;

import com.greenstone.mes.external.application.dto.cmd.FlowNoticeCmd;
import com.greenstone.mes.system.api.domain.SysUser;

import java.util.List;

public interface ProcessNoticeService {

    void sendNotice(FlowNoticeCmd noticeCmd, List<SysUser> users);

}
