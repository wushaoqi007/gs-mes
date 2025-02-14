package com.greenstone.mes.office.api;

import com.greenstone.mes.common.core.constant.ServiceNameConstants;
import com.greenstone.mes.office.api.factory.RemoteMeetingFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 用户服务
 *
 * @author ruoyi
 */
@Repository
@FeignClient(contextId = "remoteMeetingService", value = ServiceNameConstants.OFFICE_SERVICE, fallbackFactory = RemoteMeetingFallbackFactory.class)
public interface RemoteMeetingService {

    @PostMapping("/meeting/room/reserve/changeStatus")
    void changeStatus();

}
