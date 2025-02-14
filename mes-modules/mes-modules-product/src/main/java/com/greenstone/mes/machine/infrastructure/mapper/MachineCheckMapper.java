package com.greenstone.mes.machine.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineCheckPartListQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachinePartScanQuery2;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordQuery;
import com.greenstone.mes.machine.application.dto.result.MachineCheckPartR;
import com.greenstone.mes.machine.application.dto.result.MachineCheckRecord;
import com.greenstone.mes.machine.domain.entity.MachineCheckDetail;
import com.greenstone.mes.machine.infrastructure.persistence.MachineCheckDO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MachineCheckMapper extends EasyBaseMapper<MachineCheckDO> {
    List<MachineCheckPartR> selectPartList(MachineCheckPartListQuery query);

    List<MachineCheckRecord> listRecord(MachineCheckPartListQuery query);

    List<MachineCheckRecord> reworkRecord(MachineRecordQuery query);

    List<MachineCheckDetail> selectReworkDetails(MachinePartScanQuery2 scanQuery);
}
