package com.greenstone.mes.bom.service;

import com.greenstone.mes.bom.domain.Device;
import com.greenstone.mes.common.mybatisplus.IServiceWrapper;

/**
 * DeviceService接口
 *
 * @author wushaoqi
 * @date 2022-05-11-12:57
 */
public interface DeviceService extends IServiceWrapper<Device> {

    /**
     * 查询Device
     *
     * @param id 主键
     * @return Device
     */
    Device selectDeviceById(Long id);
}
