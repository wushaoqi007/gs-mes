package com.greenstone.mes.wxcp.infrastructure.config;

import com.alibaba.fastjson2.JSON;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "wechat")
public class WxCpProperties {

    private String defaultCpId;

    private Integer defaultAgentId;

    /**
     * 企业微信应用配置
     */
    private List<CpConfig> cpConfigs;

    /**
     * 不同企业的应用对应的appId
     */
    private Map<String, Map<String, Integer>> appIds;

    /**
     * 审批应用的模板信息
     */
    private List<CpSpTemplate> cpSpTemplates;

    /**
     * 用户敏感信息oauth2重定向地址
     */
    private String oauth2RedirectUri;

    /**
     * 二维码登录重定向地址
     */
    private String qrLoginRedirectUri;

    @Getter
    @Setter
    public static class CpConfig {
        /**
         * 设置企业微信的corpId
         */
        private String corpId;

        private List<AppConfig> appConfigs;
    }

    @Getter
    @Setter
    public static class AppConfig {
        /**
         * 设置企业微信应用的AgentId
         */
        private Integer agentId;

        /**
         * 应用名称
         */
        private String name;

        /**
         * 设置企业微信应用的Secret
         */
        private String secret;

        /**
         * 设置企业微信应用的token
         */
        private String token;

        /**
         * 设置企业微信应用的EncodingAESKey
         */
        private String aesKey;

    }

    @Getter
    @Setter
    public static class CpSpTemplate {
        private String cpId;

        List<SpTemplate> spTemplates;
    }

    @Getter
    @Setter
    public static class SpTemplate {

        private String templateName;

        private String templateId;

    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}