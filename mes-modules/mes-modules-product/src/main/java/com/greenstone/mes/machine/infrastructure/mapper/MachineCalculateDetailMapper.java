package com.greenstone.mes.machine.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.machine.domain.entity.MachineCalculateDetail;
import com.greenstone.mes.machine.infrastructure.persistence.MachineCalculateDetailDO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MachineCalculateDetailMapper extends EasyBaseMapper<MachineCalculateDetailDO> {
    List<MachineCalculateDetail> selectDetailBySerialNo(String serialNo);
}
