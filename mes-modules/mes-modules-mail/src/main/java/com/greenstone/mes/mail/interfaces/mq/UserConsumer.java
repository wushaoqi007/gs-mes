package com.greenstone.mes.mail.interfaces.mq;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.greenstone.mes.external.dto.result.MailboxChangeResult;
import com.greenstone.mes.mail.cmd.MailBoxAddCmd;
import com.greenstone.mes.mail.consts.MailConst;
import com.greenstone.mes.mail.domain.helper.PinyinHelper;
import com.greenstone.mes.mail.domain.service.MailBoxService;
import com.greenstone.mes.mq.consts.MqConst;
import com.greenstone.mes.mq.producer.MsgProducer;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.wxcp.domain.helper.WxcpService;
import com.greenstone.mes.wxcp.infrastructure.config.WxCpProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserConsumer {

    private final MailBoxService mailBoxService;

    private final WxCpProperties wxCpProperties;

    private final WxcpService wxcpService;

    private final MsgProducer<User> msgProducer;

    @KafkaListener(topics = MqConst.Topic.USER_CREATE, groupId = MqConst.Group.MAIL)
    public void onUserCreate(User user) {
        log.info("Receive msg, topic: {}, content: {}", MqConst.Topic.USER_CREATE, JSON.toJSONString(user));
        log.info("消息队列：新用户消息 {} {}", user.getUserId(), user.getNickName());
        createMailForNerUser(user);
    }

    @KafkaListener(topics = MqConst.Topic.USER_EMPLOYNO_ADDED, groupId = MqConst.Group.MAIL)
    public void onUserEmployNoCreate(User user) {
        log.info("Receive msg, topic: {}, content: {}", MqConst.Topic.USER_EMPLOYNO_ADDED, JSON.toJSONString(user));
        log.info("消息队列：用户添加工号消息 {} {}", user.getUserId(), user.getNickName());
        createMailForNerUser(user);
    }

    @KafkaListener(topics = MqConst.Topic.USER_DELETE, groupId = MqConst.GROUP)
    public void onUserDelete(User user) {
        log.info("Receive msg, topic: {}, content: {}", MqConst.Topic.USER_DELETE, JSON.toJSONString(user));
        if (StrUtil.isNotEmpty(user.getEmail())) {
            mailBoxService.delayedDeleteMailBox(user.getEmail(), 60);
        }
    }

    private void createMailForNerUser(User user) {
        if (!"user".equals(user.getUserType())) {
            log.info("{} 不是企业内用户，不需要创建邮箱", user.getNickName());
            return;
        }
        String namePinyin = PinyinHelper.getNamePinyin(user.getNickName());

        String password = String.valueOf(NumberUtil.generateRandomNumber(111111, 999999, 1)[0]);

        MailBoxAddCmd boxAddCmd = MailBoxAddCmd.builder().quota(1024L)
                .mailboxType(MailConst.MailBoxType.PERSONAL)
                .localPart(namePinyin)
                .userId(user.getUserId())
                .name(user.getNickName())
                .password(password)
                .password2(password).build();

        MailboxChangeResult mailBox = mailBoxService.createMailBoxForNewUser(boxAddCmd);

        String content = StrUtil.format("您的工作邮箱已创建，账号：{}，密码：{}", mailBox.getEmail(), password);
        WxCpMessage msg = WxCpMessage.TEXTCARD().title("邮箱").description(content)
                .btnTxt("使用说明").url("https://mes.wxgreenstone.com/docs/%E9%82%AE%E7%AE%B1/%E6%96%B0%E5%91%98%E5%B7%A5/")
                .toUser(user.getWxUserId()).agentId(wxCpProperties.getDefaultAgentId()).build();
        try {
            WxCpService wxCpService = wxcpService.getWxCpService(wxCpProperties.getDefaultAgentId());
            wxCpService.getMessageService().send(msg);
        } catch (WxErrorException e) {
            log.error("发送消息失败", e);
            throw new RuntimeException(e);
        }


        try {
            user.setEmail(mailBox.getEmail());
            msgProducer.send(MqConst.Topic.MAIL_CREATE, user);
        } catch (ExecutionException | InterruptedException e) {
            log.error("邮箱创建消息发送失败", e);
        }
    }

}
