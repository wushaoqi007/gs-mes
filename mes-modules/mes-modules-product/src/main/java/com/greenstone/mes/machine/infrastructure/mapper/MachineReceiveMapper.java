package com.greenstone.mes.machine.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderExportQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordQuery;
import com.greenstone.mes.machine.application.dto.result.MachineReceiveExportR;
import com.greenstone.mes.machine.application.dto.result.MachineReceiveRecord;
import com.greenstone.mes.machine.infrastructure.persistence.MachineReceiveDO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-12-08-9:46
 */
@Repository
public interface MachineReceiveMapper extends EasyBaseMapper<MachineReceiveDO> {
    List<MachineReceiveExportR> selectExportDataList(MachineOrderExportQuery query);

    List<MachineReceiveRecord> listRecord(MachineRecordQuery query);
}
