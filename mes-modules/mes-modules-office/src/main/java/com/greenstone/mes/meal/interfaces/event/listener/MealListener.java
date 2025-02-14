package com.greenstone.mes.meal.interfaces.event.listener;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.meal.interfaces.event.MealTicketUsedEvent;
import com.greenstone.mes.system.api.RemoteUserService;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.wxcp.domain.helper.WxMsgService;
import com.greenstone.mes.wxcp.infrastructure.config.WxConfig;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MealListener {

    private final WxMsgService wxMsgService;
    private final RemoteUserService userService;

    @Async
    @EventListener
    public void onTicketUsed(MealTicketUsedEvent ticketUsedEvent) {
        User user = userService.getById(ticketUsedEvent.getTicket().getReportById());
        String content = StrUtil.format("您于 {} 使用了餐券 {}", DateUtil.today(), ticketUsedEvent.getTicket().getTicketCode());
        WxCpMessage msg = WxCpMessage.TEXT().toUser(user.getWxUserId()).content(content).build();
        wxMsgService.sendMsg(WxConfig.SYSTEM, msg);
    }

}