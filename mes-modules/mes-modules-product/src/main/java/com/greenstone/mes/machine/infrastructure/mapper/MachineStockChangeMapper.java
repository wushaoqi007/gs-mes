package com.greenstone.mes.machine.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordQuery;
import com.greenstone.mes.machine.application.dto.result.MachineStockChangeRecord;
import com.greenstone.mes.machine.infrastructure.persistence.MachineStockChangeDO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MachineStockChangeMapper extends EasyBaseMapper<MachineStockChangeDO> {
    List<MachineStockChangeRecord> listRecord(MachineRecordQuery query);
}
