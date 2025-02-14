package com.greenstone.mes.job.task;

import com.greenstone.mes.base.api.RemotePartsStatService;
import com.greenstone.mes.material.dto.cmd.StatDailyCmd;
import com.greenstone.mes.material.dto.cmd.StatProgressCmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 零件统计定时任务
 *
 * @author wushaoqi
 * @date 2023-03-10-9:24
 */
@Slf4j
@Component("partsStatisticsTask")
public class PartsStatTask {

    @Autowired
    private RemotePartsStatService remotePartsStatService;

    public void dailyStat(String statisticsDate) {
        log.info("start parts daily statistics");
        remotePartsStatService.dailyStat(StatDailyCmd.builder().statisticDate(statisticsDate).build());
    }

    public void monthStat() {
        log.info("start parts month statistics");
        remotePartsStatService.monthStat();
    }

    public void weekStat() {
        log.info("start parts week statistics");
        remotePartsStatService.weekStat();
    }

    public void designerStat() {
        log.info("start parts designer month statistics");
        remotePartsStatService.designerStat();
    }

    public void partsProgressStat(String startTime, String endTime, Integer day, String unit) {
        log.info("start parts progress statistics");
        StatProgressCmd statProgressCmd = StatProgressCmd.builder().startTime(startTime).endTime(endTime).time(day).unit(unit).build();
        remotePartsStatService.partsProgressStat(statProgressCmd);
    }
}
