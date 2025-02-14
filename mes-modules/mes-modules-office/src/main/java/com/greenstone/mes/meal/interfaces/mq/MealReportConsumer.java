package com.greenstone.mes.meal.interfaces.mq;

import com.alibaba.fastjson.JSON;
import com.greenstone.mes.common.exception.BusinessException;
import com.greenstone.mes.meal.application.service.MealService;
import com.greenstone.mes.meal.infrastructure.constant.MealConst;
import com.greenstone.mes.mq.consts.MqConst;
import com.greenstone.mes.oa.application.handler.templatecard.ButtonInteractionHandler;
import com.greenstone.mes.oa.application.handler.templatecard.MultipleInteractionHandler;
import com.greenstone.mes.office.meal.dto.cmd.MealRevokeCmd;
import com.greenstone.mes.wxcp.domain.helper.WxMsgService;
import com.greenstone.mes.wxcp.domain.types.CpId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.constant.WxCpConsts;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
@Component
public class MealReportConsumer {

    private final MealService mealService;

    private final WxMsgService wxMsgService;

    private final MultipleInteractionHandler multipleInteractionHandler;
    private final ButtonInteractionHandler buttonInteractionHandler;

    @KafkaListener(topics = MqConst.Topic.WX_CALLBACK_MEAL_REPORT, groupId = MqConst.GROUP)
    public void onMessage(WxCpXmlMessage wxMessage) {
        log.info("Receive msg, topic: {}, content: {}", MqConst.Topic.WX_CALLBACK_MEAL_REPORT, JSON.toJSONString(wxMessage));

        CpId cpId = new CpId(wxMessage.getToUserName());
        Integer agentId = Integer.valueOf(wxMessage.getAgentId());
        String wxUserId = wxMessage.getFromUserName();

        // 点击事件
        if (WxCpConsts.EventType.CLICK.equals(wxMessage.getEvent())) {
            try {
                switch (wxMessage.getEventKey()) {
                    case "meal_report@lunch" -> mealService.sendMealReportCard(MealConst.MealType.LUNCH, cpId, wxUserId);
                    case "meal_report@dinner" -> mealService.sendMealReportCard(MealConst.MealType.DINNER, cpId, wxUserId);
                    // 2024年夜饭
                    case "meal_report@frd_2024" -> mealService.sendFrdReportCard(MealConst.MealType.DINNER, cpId, wxUserId);
                    case "meal_report@qrcode" -> mealService.sendMealTicket(cpId, wxUserId);
                    case "meal_report@lunch_revocation" -> {
                        MealRevokeCmd revokeCmd = MealRevokeCmd.builder().mealType(MealConst.MealType.LUNCH)
                                .wxCpId(cpId.id())
                                .wxUserId(wxUserId)
                                .day(LocalDate.now())
                                .revokeType(MealConst.ReportType.SELF_REVOKE).build();
                        mealService.selfRevoke(revokeCmd);
                    }
                    case "meal_report@dinner_revocation" -> {
                        MealRevokeCmd revokeCmd = MealRevokeCmd.builder().mealType(MealConst.MealType.DINNER)
                                .wxCpId(cpId.id())
                                .wxUserId(wxUserId)
                                .day(LocalDate.now())
                                .revokeType(MealConst.ReportType.SELF_REVOKE).build();
                        mealService.selfRevoke(revokeCmd);
                    }
                    default -> log.error("Unsupported event key: {}", wxMessage.getEventKey());
                }
            } catch (Exception e) {
                wxMsgService.sendMsg(cpId, agentId, WxCpMessage.TEXT().toUser(wxUserId).content(e.getMessage()).build());
            }
        }

        // 模板卡片事件
        if ("template_card_event".equals(wxMessage.getEvent())) {
            try {
                String cardType = wxMessage.getAllFieldsMap().get("CardType").toString();
                switch (cardType) {
                    case WxConsts.TemplateCardType.BUTTON_INTERACTION -> buttonInteractionHandler.handle(wxMessage);
                    case WxConsts.TemplateCardType.MULTIPLE_INTERACTION -> multipleInteractionHandler.handle(wxMessage);
                    default -> {
                        log.error("Unsupported cardType: " + cardType);
                    }
                }
                ;
            } catch (BusinessException e) {
                log.warn(e.getMessage());
                // 发送错误消息提示给用户
                wxMsgService.sendMsg(cpId, agentId,
                        WxCpMessage.TEXT().toUser(wxMessage.getFromUserName()).content(e.getMessage()).build());
            } catch (Exception e) {
                log.error("系统错误", e);
                wxMsgService.sendMsg(cpId, agentId,
                        WxCpMessage.TEXT().toUser(wxMessage.getFromUserName()).content("系统错误，请联系管理员。").build());

            }
        }


    }

}
