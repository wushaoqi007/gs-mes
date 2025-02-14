package com.greenstone.mes.office.api;


import com.greenstone.mes.common.core.constant.ServiceNameConstants;
import com.greenstone.mes.form.dto.cmd.ProcessResult;
import com.greenstone.mes.form.dto.result.FormBriefResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author gu_renkai
 * @date 2023/3/6 11:06
 */
@FeignClient(contextId = "remoteFormService", value = ServiceNameConstants.OFFICE_SERVICE)

public interface RemoteFormService {

    @PostMapping("/form/approve")
    void approve(@RequestBody ProcessResult processResult);

    @GetMapping("/form/definition/{formId}")
    FormBriefResult getForm(@PathVariable("formId") String formId);

}
