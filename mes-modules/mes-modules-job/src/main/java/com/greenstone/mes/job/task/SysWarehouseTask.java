package com.greenstone.mes.job.task;

import com.greenstone.mes.job.service.ISysWarehouseJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 仓库任务
 *
 * @author wushaoqi
 * @date 2022-11-01-9:50
 */
@Slf4j
@Component("sysWarehouseTask")
public class SysWarehouseTask {


    @Autowired
    private ISysWarehouseJobService warehouseJobService;
    /**
     * 库存超时提醒并通知人员
     */
    public void remind(String warehouseJobId) {
        log.info("system warehouse task start");
        warehouseJobService.timeOutRemind(warehouseJobId);
    }
}
