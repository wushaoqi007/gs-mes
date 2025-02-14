package com.greenstone.mes.mq.nacos;

import com.alibaba.fastjson2.JSON;
import com.alibaba.nacos.client.naming.event.InstancesChangeEvent;
import com.alibaba.nacos.common.notify.Event;
import com.alibaba.nacos.common.notify.NotifyCenter;
import com.alibaba.nacos.common.notify.listener.Subscriber;
import com.greenstone.mes.mq.config.MqConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;

@RequiredArgsConstructor
@Slf4j
@Component
public class NacosServiceInstanceChangeNotifier extends Subscriber<InstancesChangeEvent> {

    private final KafkaListenerEndpointRegistry registry;

    private final MqConfig mqConfig;

    @PostConstruct
    public void registerToNotifyCenter() {
        NotifyCenter.registerSubscriber(this);
        // 以下代码是无用的，就算取消注释也是无用的
//        NamingService namingService = nacosServiceManager.getNamingService(nacosDiscoveryProperties.getNacosProperties());
//        try {
//            namingService.subscribe("mes-product", new EventListener() {
//                @Override
//                public void onEvent(com.alibaba.nacos.api.naming.listener.Event event) {
//                    log.info("监听nacos的服务实例变化情况: {}", JSON.toJSONString(event));
//                }
//            });
//        } catch (NacosException e) {
//            log.error("监听nacos的服务实例变化情况失败", e);
//        }
    }


    @Override
    public void onEvent(InstancesChangeEvent event) {
        log.info("接收到微服务实例状态变化: {}", JSON.toJSONString(event));

        if (!event.getServiceName().equals(mqConfig.getAppName())) {
            return;
        }
        try {
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (InetAddress inetAddress : Collections.list(ni.getInetAddresses())) {
                    if (StringUtils.isNotEmpty(inetAddress.getHostAddress()) && (inetAddress.getHostAddress().startsWith("192.168.10") || inetAddress.getHostAddress().startsWith("172.16.10"))) {
                        String ip = inetAddress.getHostAddress();
                        boolean isOnline = event.getHosts().stream().anyMatch(hosts -> hosts.getIp().equals(ip));
                        log.info("当前ip: {}, 实例状态: {}", ip, (isOnline ? "上线" : "下线"));

                        log.info("更新MQ消费者状态");
                        if (isOnline) {
                            for (MessageListenerContainer listenerContainer : registry.getAllListenerContainers()) {
                                if (!listenerContainer.isRunning()) {
                                    listenerContainer.start();
                                }
                            }
                        } else {
                            for (MessageListenerContainer listenerContainer : registry.getAllListenerContainers()) {
                                if (listenerContainer.isRunning()) {
                                    listenerContainer.stop();
                                }
                            }
                        }
                        break;
                    }
                }

            }

        } catch (SocketException e) {
            log.error("获取网卡信息失败", e);
        }

//        try {
//            NamingService namingService = nacosServiceManager.getNamingService(nacosDiscoveryProperties.getNacosProperties());
//            // 获取当前所有的服务名
//            List<String> allServiceNames = namingService.getServicesOfServer(1, Integer.MAX_VALUE).getData();
//            for (String serviceName : allServiceNames) {
//                // 获取每个服务的所有实例
//                List<Instance> allInstances = namingService.getAllInstances(serviceName);
//                for (Instance instance : allInstances) {
//                    if (instance.isHealthy()) {
//                        // 实例状态为上线
//                        log.info("服务上线 - 服务名: {}, 实例: {}:{}", serviceName, instance.getIp(), instance.getPort());
//                    } else {
//                        // 实例状态为下线
//                        log.info("服务下线 - 服务名: {}, 实例: {}:{}", serviceName, instance.getIp(), instance.getPort());
//                    }
//                }
//            }
//        } catch (NacosException e) {
//            log.error("获取服务实例失败", e);
//        }
    }

    @Override
    public Class<? extends Event> subscribeType() {
        return InstancesChangeEvent.class;
    }

}
