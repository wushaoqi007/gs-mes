package com.greenstone.mes.base.api.factory;

import com.greenstone.mes.base.api.RemotePartsStatService;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.material.dto.cmd.StatDailyCmd;
import com.greenstone.mes.material.dto.cmd.StatProgressCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 用户服务降级处理
 *
 * @author ruoyi
 */
@Component
public class RemotePartsStatFallbackFactory implements FallbackFactory<RemotePartsStatService> {
    private static final Logger log = LoggerFactory.getLogger(RemotePartsStatFallbackFactory.class);

    @Override
    public RemotePartsStatService create(Throwable throwable) {
        log.error("基础配置服务调用失败:{}", throwable.getMessage());
        return new RemotePartsStatService() {

            @Override
            public R<String> dailyStat(StatDailyCmd statDailyCmd) {
                return R.fail("零件日统计失败:" + throwable.getMessage());
            }

            @Override
            public R<String> monthStat() {
                return R.fail("零件月统计失败:" + throwable.getMessage());
            }

            @Override
            public R<String> designerStat() {
                return R.fail("零件设计月统计失败:" + throwable.getMessage());
            }

            @Override
            public R<String> weekStat() {
                return R.fail("零件周统计失败:" + throwable.getMessage());
            }

            @Override
            public R<String> partsProgressStat(StatProgressCmd statProgressCmd) {
                return R.fail("零件进度统计失败:" + throwable.getMessage());
            }

        };
    }
}
