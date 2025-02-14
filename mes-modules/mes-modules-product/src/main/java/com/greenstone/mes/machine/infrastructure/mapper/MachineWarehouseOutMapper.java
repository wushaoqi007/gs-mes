package com.greenstone.mes.machine.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordQuery;
import com.greenstone.mes.machine.application.dto.result.MachineWarehouseOutRecord;
import com.greenstone.mes.machine.infrastructure.persistence.MachineWarehouseOutDO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MachineWarehouseOutMapper extends EasyBaseMapper<MachineWarehouseOutDO> {
    List<MachineWarehouseOutRecord> listRecord(MachineRecordQuery query);
}
