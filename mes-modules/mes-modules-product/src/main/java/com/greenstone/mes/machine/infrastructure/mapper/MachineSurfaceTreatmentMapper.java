package com.greenstone.mes.machine.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordQuery;
import com.greenstone.mes.machine.application.dto.result.MachineSurfaceTreatmentRecord;
import com.greenstone.mes.machine.infrastructure.persistence.MachineSurfaceTreatmentDO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MachineSurfaceTreatmentMapper extends EasyBaseMapper<MachineSurfaceTreatmentDO> {
    List<MachineSurfaceTreatmentRecord> listRecord(MachineRecordQuery query);
}
