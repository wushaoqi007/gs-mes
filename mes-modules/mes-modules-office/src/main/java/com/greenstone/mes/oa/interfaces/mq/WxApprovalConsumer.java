package com.greenstone.mes.oa.interfaces.mq;

import com.alibaba.fastjson.JSON;
import com.greenstone.mes.meal.application.service.MealService;
import com.greenstone.mes.mq.consts.MqConst;
import com.greenstone.mes.oa.application.service.ApprovalService;
import com.greenstone.mes.oa.domain.entity.ApprovalExtraWork;
import com.greenstone.mes.office.meal.dto.cmd.MealApplyCancelRevokeCmd;
import com.greenstone.mes.wxcp.domain.helper.WxOaService;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.SpNo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.oa.WxCpApprovalDetailResult;
import me.chanjar.weixin.cp.bean.oa.WxCpSpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@RequiredArgsConstructor
@Component
public class WxApprovalConsumer {

    private final WxOaService wxOaService;

    private final ApprovalService approvalService;
    private final MealService mealService;

    @KafkaListener(topics = MqConst.Topic.WX_CALLBACK_APPROVAL, groupId = MqConst.Group.OFFICE)
    public void onMessage(WxCpXmlMessage wxMessage) {
        log.info("Receive msg, topic: {}, content: {}", MqConst.Topic.WX_CALLBACK_APPROVAL, JSON.toJSONString(wxMessage));

        WxCpApprovalDetailResult approvalDetailResult = wxOaService.getApprovalDetail(new CpId(wxMessage.getToUserName()), new SpNo(wxMessage.getApprovalInfo().getSpNo()));

        handleApproval(wxMessage.getToUserName(), approvalDetailResult);
    }

    public void handleApproval(String cpId, WxCpApprovalDetailResult approvalDetailResult) {
        WxCpApprovalDetailResult.WxCpApprovalDetail approvalDetail = approvalDetailResult.getInfo();
        log.info("Receive wx msg event '{}' '{}'", cpId, approvalDetail.getSpNo());
        // 同步接收到的企业微信审批消息
        approvalService.sync(new CpId(cpId), approvalDetail);
        if (isCancelOvertime(approvalDetail)) {
            ApprovalExtraWork extraWork = ApprovalExtraWork.from(new CpId(cpId), approvalDetail.getSpName(), approvalDetail);
            LocalDate day = LocalDateTime.ofEpochSecond(extraWork.getStartTime().getTime() / 1000, 0, ZoneOffset.ofHours(8)).toLocalDate();
            MealApplyCancelRevokeCmd revokeCmd = MealApplyCancelRevokeCmd.builder().day(day)
                    .wxUserId(extraWork.getUserId().id())
                    .wxCpId(extraWork.getCpId().id()).build();
            mealService.sysRevoke(revokeCmd);
        }
    }

    private boolean isCancelOvertime(WxCpApprovalDetailResult.WxCpApprovalDetail approvalDetail) {
        return "加班".equals(approvalDetail.getSpName()) &&
                (approvalDetail.getSpStatus() == WxCpSpStatus.REJECTED
                        || approvalDetail.getSpStatus() == WxCpSpStatus.UNDONE
                        || approvalDetail.getSpStatus() == WxCpSpStatus.PASS_UNDONE);
    }

}
