package com.greenstone.mes.wxcp.domain.service.impl;

import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.system.api.RemoteUserService;
import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.workflow.cmd.FlowCommitCmd;
import com.greenstone.mes.wxcp.cmd.WxFlowCommitCmd;
import com.greenstone.mes.wxcp.domain.helper.SpHelper;
import com.greenstone.mes.wxcp.domain.helper.WxcpService;
import com.greenstone.mes.wxcp.domain.service.WxFlowService;
import com.greenstone.mes.wxcp.infrastructure.config.WxConfig;
import com.greenstone.mes.wxcp.infrastructure.mapper.FlwControlMapper;
import com.greenstone.mes.wxcp.infrastructure.persistence.ProcessControlPo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.bean.oa.WxCpOaApplyEventRequest;
import me.chanjar.weixin.cp.bean.oa.applydata.ApplyDataContent;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class WxFlowServiceImpl implements WxFlowService {

    private final FlwControlMapper flwControlMapper;
    private final RemoteUserService userService;
    private final SpHelper spHelper;
    private final WxcpService wxcpService;
    private final WxConfig wxConfig;

    @Override
    public String commit(WxFlowCommitCmd commitCmd) {
        SysUser applyUser = userService.getUser(SysUser.builder().userId(commitCmd.getApplyUserId()).build());

        WxCpOaApplyEventRequest request = buildApplyRequest(commitCmd, applyUser);
        String spNo;
        try {
            log.debug("提交企业微信审批: {}", commitCmd);
            spNo = wxcpService.getWxCpService(applyUser.getMainWxcpId(), wxConfig.getAgentId(WxConfig.SYSTEM)).getOaService().apply(request);
            log.info("审批提交成功, 审批编号: " + spNo);
        } catch (WxErrorException e) {
            throw new RuntimeException("提交企业微信审批失败", e);
        }
        return spNo;
    }

    private WxCpOaApplyEventRequest buildApplyRequest(WxFlowCommitCmd commitCmd,  SysUser applyUser) {
        WxCpOaApplyEventRequest request = new WxCpOaApplyEventRequest();

        // 设置申请人和审批模板id
        request.setCreatorUserId(applyUser.getWxUserId());
        request.setTemplateId(commitCmd.getTemplateId());
        // 使用此模板在管理后台设置的审批流程
        request.setUseTemplateApprover(1);

        WxCpOaApplyEventRequest.ApplyData applyData = new WxCpOaApplyEventRequest.ApplyData();
        request.setApplyData(applyData);
        List<ApplyDataContent> contents = new ArrayList<>();
        applyData.setContents(contents);

        List<ProcessControlPo> controlPos = flwControlMapper.list(ProcessControlPo.builder().templateId(commitCmd.getTemplateId()).build());
        for (ProcessControlPo control : controlPos) {
            FlowCommitCmd.Attr cmdAttr = commitCmd.getAttrs().stream().filter(attr -> attr.getName().equals(control.getAttrName())).findFirst().orElse(null);
            if (cmdAttr == null) {
                if (control.getRequired()) {
                    throw new ServiceException("企业微信审批提交时缺少必要的属性值: " + control.getTitle() + "(" + control.getAttrName() + ")");
                } else {
                    continue;
                }
            }
            ApplyDataContent content = spHelper.buildControl(control.getControlId(), control.getControl(), control.getType(), control.getMode(),
                    cmdAttr.getValue());
            contents.add(content);
        }
        return request;
    }

}
