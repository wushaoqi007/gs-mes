package com.greenstone.mes.wxcp.domain.handler;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.mq.consts.MqConst;
import com.greenstone.mes.mq.producer.MsgProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 模板卡片消息处理器
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class TemplateCardHandle extends AbstractHandler {

    private final MsgProducer<WxCpXmlMessage> msgProducer;

    @Override
    public WxCpXmlOutMessage handle(WxCpXmlMessage wxMessage, Map<String, Object> context,
                                    WxCpService wxCpService, WxSessionManager sessionManager) throws WxErrorException {
        // 处理模板消息
        // 所有的模板消息都需要有taskId，且格式为 业务类型@业务数据@uuid，@为分隔符，不能出现在分类、uuid和业务数据中
        // taskId 样例：meal_report@1-1@36467ac8-af3e-4e39-9a00-35ccf068043f
        // 其中 业务类型（meal_report）作为消息的tag

        // taskId不能是空的
        if (StrUtil.isBlank(wxMessage.getTaskId())) {
            log.error("TemplateCard taskId can not be empty");
            return null;
        }

        // taskId 必须包含@来分割业务类型
        if (!wxMessage.getTaskId().contains("@")) {
            log.error("TemplateCard taskId is incorrect: {}", wxMessage.getTaskId());
            return null;
        }

        String tag = wxMessage.getTaskId().substring(0, wxMessage.getTaskId().indexOf("@"));
        try {
            msgProducer.send(MqConst.TopicPrefix.WXCP_CALLBACK_TOPIC, tag, wxMessage);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return null;
    }


}
