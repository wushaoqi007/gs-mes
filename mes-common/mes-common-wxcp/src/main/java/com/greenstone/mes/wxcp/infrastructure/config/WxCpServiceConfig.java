package com.greenstone.mes.wxcp.infrastructure.config;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.val;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author gu_renkai
 * @date 2022-8-1 9:51
 */
@EnableConfigurationProperties(WxCpProperties.class)
@RequiredArgsConstructor
@Configuration
public class WxCpServiceConfig {

    private final WxCpProperties multiProperties;

    private static final Map<String, Map<Integer, WxCpService>> cpServices = Maps.newHashMap();

    public Map<String, Map<Integer, WxCpService>> getCpServices() {
        return cpServices;
    }

    public static WxCpService getCpService(String cpId, Integer agentId) {
        if (StrUtil.isBlank(cpId)) {
            throw new RuntimeException("获取企业微信服务失败，企业id不能为空");
        }
        if (agentId == null) {
            throw new RuntimeException("获取企业微信服务失败，应用id不能为空");
        }
        Map<Integer, WxCpService> integerWxCpServiceMap = cpServices.get(cpId);
        if (integerWxCpServiceMap == null) {
            throw new RuntimeException("获取企业微信服务失败，企业id不存在: " + cpId);
        }
        WxCpService cpService = integerWxCpServiceMap.get(agentId);
        if (cpService == null) {
            throw new RuntimeException("获取企业微信服务失败，应用id不存在: " + agentId);
        }
        return cpService;
    }

    /**
     * 获取服务列表
     *
     * @param agentId 服务ID
     * @return 服务列表
     */
    public static List<WxCpService> getCpServices(Integer agentId) {
        List<WxCpService> services = new ArrayList<>();
        cpServices.forEach((cpId, cpServices) -> services.add(cpServices.get(agentId)));
        return services;
    }

    /**
     * 获取企业和服务
     *
     * @param agentId 服务ID
     * @return 企业和服务
     */
    public static Map<String, WxCpService> getCpServiceMap(Integer agentId) {
        Map<String, WxCpService> serviceMap = new HashMap<>();
        cpServices.forEach((cpId, cpServices) -> serviceMap.put(cpId, cpServices.get(agentId)));
        return serviceMap;
    }

    @PostConstruct
    public void initServices() {
        for (WxCpProperties.CpConfig cpConfig : multiProperties.getCpConfigs()) {
            Map<Integer, WxCpService> services = cpConfig.getAppConfigs().stream().map(a -> {
                val configStorage = new WxCpDefaultConfigImpl();
                configStorage.setCorpId(cpConfig.getCorpId());
                configStorage.setAgentId(a.getAgentId());
                configStorage.setCorpSecret(a.getSecret());
                configStorage.setToken(a.getToken());
                configStorage.setAesKey(a.getAesKey());
                val service = new WxCpServiceImpl();
                service.setWxCpConfigStorage(configStorage);
                return service;
            }).collect(Collectors.toMap(service -> service.getWxCpConfigStorage().getAgentId(), a -> a));
            cpServices.put(cpConfig.getCorpId(), services);
        }
    }

}
