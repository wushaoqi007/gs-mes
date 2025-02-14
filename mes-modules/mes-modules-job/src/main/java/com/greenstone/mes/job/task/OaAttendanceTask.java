package com.greenstone.mes.job.task;

import cn.hutool.core.date.DateUtil;
import com.greenstone.mes.base.api.RemoteOaService;
import com.greenstone.mes.oa.dto.AttendanceCalcCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * OA考勤相关任务
 */
@Slf4j
@Component("oaAttendanceTask")
public class OaAttendanceTask {

    @Autowired
    private RemoteOaService remoteOaService;

    /**
     * 昨日考勤统计
     */
    public void calcYesterdayAttendance() {
        log.info("Task start: calcYesterdayAttendance");
        remoteOaService.calcYesterdayAttendance();
    }

    public void calcAndSave(String start, String end, String cpId, String userId, Boolean refreshCache, Boolean quickCalc) {
        log.info("Task start: calcAndSave，{} {}", start, end);
        AttendanceCalcCommand command = AttendanceCalcCommand.builder().start(DateUtil.parse(start, "yyyy-MM-dd"))
                .end(DateUtil.parse(end, "yyyy-MM-dd")).cpId(cpId)
                .userId(userId).refreshCache(refreshCache).quickCalc(quickCalc).build();
        remoteOaService.calcAndSaveAsync(command);
    }

}
