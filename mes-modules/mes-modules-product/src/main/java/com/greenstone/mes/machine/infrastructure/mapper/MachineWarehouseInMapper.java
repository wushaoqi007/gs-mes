package com.greenstone.mes.machine.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordQuery;
import com.greenstone.mes.machine.application.dto.result.MachineWarehouseInRecord;
import com.greenstone.mes.machine.infrastructure.persistence.MachineWarehouseInDO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MachineWarehouseInMapper extends EasyBaseMapper<MachineWarehouseInDO> {
    List<MachineWarehouseInRecord> listRecord(MachineRecordQuery query);
}
