package com.greenstone.mes.oa.application.handler.templatecard;

import com.alibaba.fastjson.JSONObject;
import com.greenstone.mes.ces.dto.cmd.AppStatusChangeCmd;
import com.greenstone.mes.common.exception.BusinessException;
import com.greenstone.mes.common.redis.service.RedisLock;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.reimbursement.application.helper.ReimbursementHelper;
import com.greenstone.mes.reimbursement.application.service.ReimbursementAppService;
import com.greenstone.mes.wxcp.domain.helper.WxMsgService;
import com.greenstone.mes.wxcp.domain.types.CpId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class ButtonInteractionHandler{

    private final ReimbursementHelper reimbursementHelper;
    private final ReimbursementAppService reimbursementAppService;
    private final WxMsgService wxMsgService;
    private final RedisLock redisLock;

    /**
     * 按钮交互型卡片处理器
     */
    public WxCpXmlOutMessage handle(WxCpXmlMessage wxMessage) throws WxErrorException {
        CpId cpId = new CpId(wxMessage.getToUserName());
        Integer agentId = Integer.valueOf(wxMessage.getAgentId());
        String lockKey = "reimbursement_approve_" + wxMessage.getFromUserName();
        if (!redisLock.lock(lockKey, 8)) {
            wxMsgService.sendMsg(cpId, agentId, WxCpMessage.TEXT().toUser(wxMessage.getFromUserName()).content("处理中，请稍后。").build());
            return WxCpXmlOutMessage.TEXT().content(null)
                    .fromUser(wxMessage.getToUserName()).toUser(wxMessage.getFromUserName())
                    .build();
        }

        String taskId = wxMessage.getTaskId();
        // 报销单号
        String serialNo = reimbursementHelper.getSerialNo(taskId);
        String eventKey = wxMessage.getEventKey();
        ProcessStatus status = null;
        String cardUpdateContent = "已通过";
        if ("reject".equals(eventKey)) {
            status = ProcessStatus.REJECTED;
            cardUpdateContent = "已驳回";
        }
        if ("approve".equals(eventKey)) {
            status = ProcessStatus.APPROVED;
        }
        if (status == null) {
            log.error("系统错误:审批状态为空");
            wxMsgService.sendMsg(cpId, agentId,
                    WxCpMessage.TEXT().toUser(wxMessage.getFromUserName()).content("系统错误，请联系管理员。").build());
        }
        AppStatusChangeCmd appStatusChangeCmd = AppStatusChangeCmd.builder().status(status).serialNos(List.of(serialNo)).build();
        try {
            reimbursementAppService.changeStatus(appStatusChangeCmd);
        } catch (BusinessException e) {
            cardUpdateContent = "审批失败";
            log.warn("系统错误", e);
            // 发送错误消息提示给用户
            wxMsgService.sendMsg(cpId, agentId,
                    WxCpMessage.TEXT().toUser(wxMessage.getFromUserName()).content(e.getMessage()).build());
        } catch (Exception e) {
            cardUpdateContent = "审批失败";
            log.error("系统错误", e);
            wxMsgService.sendMsg(cpId, agentId,
                    WxCpMessage.TEXT().toUser(wxMessage.getFromUserName()).content("系统错误，请联系管理员。").build());
        }

        // 关闭此卡片
        JSONObject message = new JSONObject();
        message.put("userids", List.of(wxMessage.getFromUserName()));
        message.put("agentid", wxMessage.getAgentId());
        message.put("response_code", wxMessage.getAllFieldsMap().get("ResponseCode"));
        message.put("button", Map.of("replace_name", cardUpdateContent));
        wxMsgService.updateTemplateCard(cpId, agentId, message.toJSONString());

        redisLock.unlock(lockKey);
        return WxCpXmlOutMessage.TEXT().content(null)
                .fromUser(wxMessage.getToUserName()).toUser(wxMessage.getFromUserName())
                .build();
    }
}
