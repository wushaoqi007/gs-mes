package com.greenstone.mes.bom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.greenstone.mes.bom.domain.Device;
import org.springframework.stereotype.Repository;

/**
 * DeviceMapper接口
 *
 * @author wushaoqi
 * @date 2022-05-11-13:02
 */
@Repository
public interface DeviceMapper extends BaseMapper<Device> {
    /**
     * 查询Device
     *
     * @param id Device主键
     * @return Device
     */
    Device selectDeviceById(Long id);
}
