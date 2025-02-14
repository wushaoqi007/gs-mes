package com.greenstone.mes.wxcp.interfaces.rest;


import com.greenstone.mes.common.security.annotation.RawResponse;
import com.greenstone.mes.wxcp.infrastructure.config.WxCpRouterConfig;
import com.greenstone.mes.wxcp.infrastructure.config.WxCpServiceConfig;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;
import me.chanjar.weixin.cp.util.crypto.WxCpCryptUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/wxcp/portal/{cpId}/{agentId}")
public class WxPortalApi {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @RawResponse
    @GetMapping(produces = "text/plain;charset=utf-8")
    public String authGet(@PathVariable String cpId,
                          @PathVariable Integer agentId,
                          @RequestParam(name = "msg_signature", required = false) String signature,
                          @RequestParam(name = "timestamp", required = false) String timestamp,
                          @RequestParam(name = "nonce", required = false) String nonce,
                          @RequestParam(name = "echostr", required = false) String echostr) {
        this.logger.info("\n接收到来自微信服务器的认证消息：signature = [{}], timestamp = [{}], nonce = [{}], echostr = [{}]",
                signature, timestamp, nonce, echostr);

        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            throw new IllegalArgumentException("请求参数非法，请核实!");
        }

        final WxCpService wxCpService = WxCpServiceConfig.getCpService(cpId, agentId);

        if (wxCpService.checkSignature(signature, timestamp, nonce, echostr)) {
            return new WxCpCryptUtil(wxCpService.getWxCpConfigStorage()).decrypt(echostr);
        }

        return "非法请求";
    }

    @RawResponse
    @PostMapping("/test")
    public String testPost(@PathVariable String cpId,
                           @PathVariable Integer agentId,
                           @RequestBody WxCpXmlMessage inMessage) {
        logger.info("接收到测试数据：{}", inMessage);

        WxCpXmlOutMessage outMessage = this.route(cpId, agentId, inMessage);
        if (outMessage == null) {
            return "测试数据处理完成，无返回";
        }

        return "测试数据处理完成，返回值：" + outMessage;
    }

    @RawResponse
    @PostMapping(produces = "application/xml; charset=UTF-8")
    public String post(@PathVariable String cpId,
                       @PathVariable Integer agentId,
                       @RequestBody String requestBody,
                       @RequestParam("msg_signature") String signature,
                       @RequestParam("timestamp") String timestamp,
                       @RequestParam("nonce") String nonce) {
        logger.info("Received msg from wechat with cpId:{}, agentId:{}", cpId, agentId);
        this.logger.info("\nWechat msg info: [signature=[{}], timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
                signature, timestamp, nonce, requestBody);
        final WxCpService wxCpService = WxCpServiceConfig.getCpService(cpId, agentId);
        WxCpXmlMessage inMessage = WxCpXmlMessage.fromEncryptedXml(requestBody, wxCpService.getWxCpConfigStorage(),
                timestamp, nonce, signature);
        WxCpXmlOutMessage outMessage = this.route(cpId, agentId, inMessage);
        if (outMessage == null) {
            return "";
        }

        return outMessage.toEncryptedXml(wxCpService.getWxCpConfigStorage());
    }

    private WxCpXmlOutMessage route(String cpId, Integer agentId, WxCpXmlMessage message) {
        try {
            return WxCpRouterConfig.getRouters(cpId).get(agentId).route(message);
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }

        return null;
    }

}
