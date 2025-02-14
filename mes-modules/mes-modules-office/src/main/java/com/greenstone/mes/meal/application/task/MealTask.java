package com.greenstone.mes.meal.application.task;

import com.greenstone.mes.meal.application.service.MealService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableScheduling
public class MealTask {

    private final MealService mealService;

    /**
     * 报餐自动截止
     */
    @Scheduled(cron = "0 0 9,14 * * ?")
    private void autoStopReport() {
        log.info("autoStopReport start");
        mealService.stopReport();
        log.info("autoStopReport end");
    }

    /**
     * 报餐自动截止1
     */
    @Scheduled(cron = "0 15 12 * * ?")
    private void autoStopAdditionalReport1() {
        log.info("autoStopAdditionalReport1 start");
        mealService.stopAdditionalReport();
        log.info("autoStopAdditionalReport1 end");
    }

    /**
     * 报餐自动截止2
     */
    @Scheduled(cron = "0 50 17 * * ?")
    private void autoStopAdditionalReport2() {
        log.info("autoStopAdditionalReport2 start");
        mealService.stopAdditionalReport();
        log.info("autoStopAdditionalReport2 end");
    }

    /**
     * 午餐报餐提醒
     */
//    @Scheduled(cron = "0 0 8 * * ?")
    private void lunchReportRemind() {
        log.info("lunchReportRemind start");
        mealService.lunchReportRemind();
        log.info("lunchReportRemind end");
    }

}
