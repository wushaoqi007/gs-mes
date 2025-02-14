package com.greenstone.mes.mail.interfaces.mq;

import com.alibaba.fastjson.JSON;
import com.greenstone.mes.mail.cmd.MailBoxEditCmd;
import com.greenstone.mes.mail.domain.service.MailBoxService;
import com.greenstone.mes.mail.infrastructure.mapper.MailBoxMapper;
import com.greenstone.mes.mail.infrastructure.persistence.MailBox;
import com.greenstone.mes.mq.consts.MqConst;
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

@Slf4j
@RequiredArgsConstructor
@Component
public class WxApprovalConsumer {

    private final WxOaService wxOaService;
    private final MailBoxService mailBoxService;
    private final MailBoxMapper mailBoxMapper;


    @KafkaListener(topics = MqConst.Topic.WX_CALLBACK_APPROVAL, groupId = MqConst.Group.MAIL)
    public void onMessage(WxCpXmlMessage wxMessage) {
        log.info("Receive msg, topic: {}, content: {}", MqConst.Topic.WX_CALLBACK_APPROVAL, JSON.toJSONString(wxMessage));

        WxCpApprovalDetailResult approvalDetailResult = wxOaService.getApprovalDetail(new CpId(wxMessage.getToUserName()), new SpNo(wxMessage.getApprovalInfo().getSpNo()));

        handleApproval(approvalDetailResult);
    }

    public void handleApproval(WxCpApprovalDetailResult approvalDetailResult) {

        WxCpApprovalDetailResult.WxCpApprovalDetail approvalDetail = approvalDetailResult.getInfo();

        if ("邮箱扩容".equals(approvalDetail.getSpName()) && approvalDetail.getSpStatus() == WxCpSpStatus.PASSED) {

            MailBox mailBox = mailBoxMapper.getOneOnly(MailBox.builder().wxUserId(approvalDetailResult.getInfo().getApplier().getUserId()).build());
            if (mailBox == null) {
                log.error("扩容失败，微信用户 {} 的邮箱不存在", approvalDetailResult.getInfo().getApplier().getUserId());
            }

            MailBoxEditCmd editCmd = MailBoxEditCmd.builder().email(mailBox.getEmail()).quota(3072L).build();
            mailBoxService.editMailBox(editCmd);
        }

    }


}
