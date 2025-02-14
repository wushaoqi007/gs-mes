package com.greenstone.mes.oa.application.handler.templatecard;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.greenstone.mes.common.exception.BusinessException;
import com.greenstone.mes.common.redis.service.RedisLock;
import com.greenstone.mes.external.workwx.msg.SelectedItem;
import com.greenstone.mes.external.workwx.msg.SelectedItems;
import com.greenstone.mes.mail.consts.MailConst;
import com.greenstone.mes.meal.application.helper.MealHelper;
import com.greenstone.mes.meal.application.service.MealService;
import com.greenstone.mes.meal.infrastructure.constant.MealConst;
import com.greenstone.mes.office.meal.dto.cmd.MealReportCmd;
import com.greenstone.mes.wxcp.domain.helper.WxMsgService;
import com.greenstone.mes.wxcp.domain.types.CpId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class MultipleInteractionHandler {

    private final MealHelper mealHelper;
    private final MealService mealService;
    private final WxMsgService wxMsgService;
    private final RedisLock redisLock;

    /**
     * 多项选择型卡片处理器
     */
    public WxCpXmlOutMessage handle(WxCpXmlMessage wxMessage) throws WxErrorException {
        CpId cpId = new CpId(wxMessage.getToUserName());
        Integer agentId = Integer.valueOf(wxMessage.getAgentId());

        String lockKey = "meal_report@" + wxMessage.getFromUserName();

        if (!redisLock.lock(lockKey, 10)) {
            wxMsgService.sendMsg(cpId, agentId, WxCpMessage.TEXT().toUser(wxMessage.getFromUserName()).content("处理中，请稍后。").build());
            return WxCpXmlOutMessage.TEXT().content(null)
                    .fromUser(wxMessage.getToUserName()).toUser(wxMessage.getFromUserName())
                    .build();
        }

        SelectedItems selectedItems = JSON.parseObject(JSON.toJSONString(wxMessage.getAllFieldsMap().get("SelectedItems")),
                SelectedItems.class);
        // 吃不吃
        boolean isEat = selectedItems.getSelectedItem().stream().filter(s -> s.getQuestionKey().equals("eat"))
                .findFirst().map(SelectedItem::getOptionIds)
                .map(o -> o.getOptionId().get(0))
                .map("y"::equals)
                .orElse(false);
        // 吃几份
        int mealNum;
        if (isEat) {
            mealNum = selectedItems.getSelectedItem().stream().filter(s -> s.getQuestionKey().equals("num"))
                    .findFirst().map(SelectedItem::getOptionIds)
                    .map(o -> o.getOptionId().get(0))
                    .map(Integer::parseInt)
                    .orElse(0);
        } else {
            mealNum = 0;
        }

        MealReportCmd mealReportCmd = MealReportCmd.builder()
                .haveMeal(isEat)
                .mealNum(mealNum)
                .wxCpId(wxMessage.getToUserName())
                .wxUserId(wxMessage.getFromUserName()).build();

        // 设置用餐类型、报餐类型和日期
        String taskId = wxMessage.getTaskId();
        mealHelper.setMealReportTypeAndDate(mealReportCmd, taskId);

        String cardUpdateContent = "已提交";
        try {
            mealService.selfReport(mealReportCmd);
        } catch (BusinessException e) {
            cardUpdateContent = "提交失败";
            log.warn("系统错误", e);
            // 发送错误消息提示给用户
            wxMsgService.sendMsg(cpId, agentId,
                    WxCpMessage.TEXT().toUser(wxMessage.getFromUserName()).content(e.getMessage()).build());
        } catch (Exception e) {
            cardUpdateContent = "提交失败";
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
