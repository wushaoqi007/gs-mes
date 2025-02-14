package com.greenstone.mes.system.api;

import com.greenstone.mes.common.core.constant.ServiceNameConstants;
import com.greenstone.mes.system.dto.result.FunctionResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.constraints.NotBlank;

@FeignClient(contextId = "remoteFunctionService", value = ServiceNameConstants.SYSTEM_SERVICE)
public interface RemoteFunctionService {

    @GetMapping("/functions/{id}")
    FunctionResult getFunction(@PathVariable("id") @NotBlank(message = "请指定需要查询的功能") Long id);

}
