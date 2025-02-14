package com.greenstone.mes.machine.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordFuzzyQuery;
import com.greenstone.mes.machine.application.dto.result.MachineCheckedTakeRecord;
import com.greenstone.mes.machine.infrastructure.persistence.MachineCheckedTakeDO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MachineCheckedTakeMapper extends EasyBaseMapper<MachineCheckedTakeDO> {
    List<MachineCheckedTakeRecord> listRecord(MachineRecordFuzzyQuery query);
}
