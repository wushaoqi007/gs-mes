package com.greenstone.mes.machine.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordFuzzyQuery;
import com.greenstone.mes.machine.application.dto.result.MachineCheckTakeRecord;
import com.greenstone.mes.machine.infrastructure.persistence.MachineCheckTakeDO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MachineCheckTakeMapper extends EasyBaseMapper<MachineCheckTakeDO> {
    List<MachineCheckTakeRecord> listRecord(MachineRecordFuzzyQuery query);
}
