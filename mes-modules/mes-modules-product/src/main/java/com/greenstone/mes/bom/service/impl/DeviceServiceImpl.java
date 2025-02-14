package com.greenstone.mes.bom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.bom.domain.Device;
import com.greenstone.mes.bom.mapper.DeviceMapper;
import com.greenstone.mes.bom.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * DeviceService业务层处理
 *
 * @author wushaoqi
 * @date 2022-05-11-13:00
 */
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService {

    @Autowired
    private DeviceMapper deviceMapper;


    @Override
    public Device selectDeviceById(Long id) {
        return deviceMapper.selectDeviceById(id);
    }
}
