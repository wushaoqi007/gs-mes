package com.greenstone.mes.material.application.service;

import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.request.WarehouseBindProjectCmd;
import com.greenstone.mes.material.request.WarehouseBindReq;

/**
 * @author wushaoqi
 * @date 2022-11-22-9:37
 */
public interface BaseWarehouseManager {

    BaseWarehouse bindWarehouse(WarehouseBindReq bindReq);

    void checkWarehouse(Long[] ids);

    BaseWarehouse bindProject(WarehouseBindProjectCmd bindProjectCmd);

    void unBindProject(WarehouseBindProjectCmd bindProjectCmd);
}
