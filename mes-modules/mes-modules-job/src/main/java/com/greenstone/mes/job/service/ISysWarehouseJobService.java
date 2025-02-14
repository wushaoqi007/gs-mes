package com.greenstone.mes.job.service;

import com.greenstone.mes.common.mybatisplus.IServiceWrapper;
import com.greenstone.mes.system.domain.SysWarehouseJob;
import com.greenstone.mes.system.dto.cmd.SysWarehouseJobAddReq;
import com.greenstone.mes.system.dto.cmd.SysWarehouseJobEditReq;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2022-11-01-8:39
 */
public interface ISysWarehouseJobService extends IServiceWrapper<SysWarehouseJob> {

    void insertJob(SysWarehouseJobAddReq job);

    /**
     * 库存超时提醒并通知人员
     */
    void timeOutRemind(String warehouseJobId);

    void updateJob(SysWarehouseJobEditReq job);

    void deleteJobById(Long id);

    void changeStatus(SysWarehouseJob job);

    List<SysWarehouseJob> selectJobList(SysWarehouseJob job);

    SysWarehouseJob getJobById(Long id);
}
