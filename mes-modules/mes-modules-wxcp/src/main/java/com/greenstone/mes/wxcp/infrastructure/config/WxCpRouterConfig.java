package com.greenstone.mes.wxcp.infrastructure.config;

import com.google.common.collect.Maps;
import com.greenstone.mes.wxcp.domain.handler.*;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.constant.WxCpConsts;
import me.chanjar.weixin.cp.message.MessageRouter;
import me.chanjar.weixin.cp.message.WxCpMessageRouter;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 创建企业微信消息的处理路由
 *
 * @author gu_renkai
 * @date 2022-8-1 9:51
 */
@RequiredArgsConstructor
@Configuration
public class WxCpRouterConfig {
    private final LogHandler logHandler;
    private final NullHandler nullHandler;
    private final LocationHandler locationHandler;
    private final MenuHandler menuHandler;
    private final ApprovalHandler approvalHandler;
    private final ContactChangeHandler contactChangeHandler;
    private final UnsubscribeHandler unsubscribeHandler;
    private final SubscribeHandler subscribeHandler;
    private final TemplateCardHandle templateCardHandle;

    private static final Map<String, Map<Integer, WxCpMessageRouter>> routersMap = Maps.newHashMap();

    private final WxCpServiceConfig wxCpServiceConfig;

    public static Map<Integer, WxCpMessageRouter> getRouters(String cpId) {
        return routersMap.get(cpId);
    }

    @PostConstruct
    public void initServices() {
        wxCpServiceConfig.getCpServices().forEach((cropId, agnetServiceMap) -> {
            Map<Integer, WxCpMessageRouter> routers = routersMap.computeIfAbsent(cropId, k -> new HashMap<>());
            agnetServiceMap.forEach((agentId, wxCpService) -> routers.put(agentId, newRouter(wxCpService)));
        });
    }

    private WxCpMessageRouter newRouter(WxCpService wxCpService) {
        MessageRouter newRouter = new MessageRouter(wxCpService);

        // 记录所有事件的日志 （异步执行）
        newRouter.rule().handler(this.logHandler).next();

        // 菜单点击事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.MenuButtonType.CLICK).handler(this.menuHandler).end();

        // 点击菜单链接事件（这里使用了一个空的处理器，可以根据自己需要进行扩展）
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.MenuButtonType.VIEW).handler(this.nullHandler).end();

        // 关注事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.EventType.SUBSCRIBE).handler(this.subscribeHandler).end();

        // 取消关注事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.EventType.UNSUBSCRIBE).handler(this.unsubscribeHandler).end();

        // 上报地理位置事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.EventType.LOCATION).handler(this.locationHandler).end();

        // 接收地理位置消息
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.LOCATION)
                .handler(this.locationHandler).end();

        // 扫码事件（这里使用了一个空的处理器，可以根据自己需要进行扩展）
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.EventType.SCAN).handler(this.nullHandler).end();

        // 通讯录变更事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxCpConsts.EventType.CHANGE_CONTACT).handler(this.contactChangeHandler).end();

        // 进入应用事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxCpConsts.EventType.ENTER_AGENT).handler(new EnterAgentHandler()).end();

        // 接收审批消息事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxCpConsts.EventType.SYS_APPROVAL_CHANGE).handler(this.approvalHandler).end();

        // 模板卡片事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event("template_card_event").handler(this.templateCardHandle).end();

        return newRouter;
    }
}
