package com.greenstone.mes.base.api;

import com.greenstone.mes.base.api.factory.RemotePartOrderFallbackFactory;
import com.greenstone.mes.common.core.constant.ServiceNameConstants;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.material.request.PartOrderAddReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remotePartOrderService", value = ServiceNameConstants.PRODUCT_SERVICE, fallbackFactory = RemotePartOrderFallbackFactory.class)
public interface RemotePartOrderService {

    @PostMapping("/part/order")
    R<Long> addPartOrder(@RequestBody PartOrderAddReq partOrderAddReq);

}
