package com.greenstone.mes.wxcp.infrastructure.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gu_renkai
 * @date 2022/8/19 14:50
 */
@Getter
@Setter
@Configuration
@RequiredArgsConstructor
public class WxConfig {
    private final WxCpProperties wxCpProperties;

    public static String ATTENDANCE = "kq";

    public static String APPLICATION = "sp";

    public static String CONTACTS = "txl";

    public static String SYSTEM = "xt";

    public static Map<String, Map<String, Integer>> cpAgentnameAgentidMap;

    /**
     * 审批应用的模板信息
     */
    @PostConstruct
    public void init() {
        cpAgentnameAgentidMap = new HashMap<>();
        for (WxCpProperties.CpConfig cpConfig : wxCpProperties.getCpConfigs()) {
            Map<String, Integer> appMap = new HashMap<>();
            for (WxCpProperties.AppConfig appConfig : cpConfig.getAppConfigs()) {
                appMap.put(appConfig.getName(), appConfig.getAgentId());
            }
            cpAgentnameAgentidMap.put(cpConfig.getCorpId(), appMap);
        }
        cpSpTemplates = wxCpProperties.getCpSpTemplates();

    }

    public String getDefaultCpId() {
        return wxCpProperties.getDefaultCpId();
    }

    public Integer getDefaultAgentId() {
        return wxCpProperties.getDefaultAgentId();
    }

    public String getOauth2RedirectUri() {
        return wxCpProperties.getOauth2RedirectUri();
    }

    public String getQrLoginRedirectUri() {
        return wxCpProperties.getQrLoginRedirectUri();
    }

    public Integer getAgentId(String agentName) {
        return cpAgentnameAgentidMap.get(getDefaultCpId()).get(agentName);
    }

    public Integer getAgentId(String cpId, String agentName) {
        return cpAgentnameAgentidMap.get(cpId).get(agentName);
    }

    /**
     * 审批应用的模板信息
     */
    private List<WxCpProperties.CpSpTemplate> cpSpTemplates;

    public WxCpProperties.SpTemplate getTemplate(String cpId, String templateName) {
        final WxCpProperties.SpTemplate[] template = {null};
        cpSpTemplates.stream().filter(c -> c.getCpId().equals(cpId)).findFirst()
                .flatMap(cpSpTemplate -> cpSpTemplate.getSpTemplates().stream().filter(s -> s.getTemplateName().equals(templateName))
                        .findFirst()).ifPresent(spTemplate -> template[0] = spTemplate);
        if (template[0] == null) {
            throw new RuntimeException("无法找到审批模板" + cpId + "-" + templateName);
        }
        return template[0];
    }



}
