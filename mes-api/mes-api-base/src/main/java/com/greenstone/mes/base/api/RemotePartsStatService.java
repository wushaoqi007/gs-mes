package com.greenstone.mes.base.api;

import com.greenstone.mes.base.api.factory.RemotePartsStatFallbackFactory;
import com.greenstone.mes.common.core.constant.ServiceNameConstants;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.material.dto.cmd.StatDailyCmd;
import com.greenstone.mes.material.dto.cmd.StatProgressCmd;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remotePartsStatService", value = ServiceNameConstants.PRODUCT_SERVICE, fallbackFactory = RemotePartsStatFallbackFactory.class)
public interface RemotePartsStatService {

    @PostMapping("/stat/daily")
    R<String> dailyStat(@RequestBody StatDailyCmd statDailyCmd);

    @PostMapping("/stat/month")
    R<String> monthStat();

    @PostMapping("/stat/month/designer")
    R<String> designerStat();

    @PostMapping("/stat/week")
    R<String> weekStat();

    @PostMapping("/stat/parts/progress")
    R<String> partsProgressStat(@RequestBody StatProgressCmd statProgressCmd);
}
