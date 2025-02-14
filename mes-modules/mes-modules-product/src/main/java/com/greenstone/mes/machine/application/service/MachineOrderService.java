package com.greenstone.mes.machine.application.service;

import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderContractExportQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderExportQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderProgressQuery;
import com.greenstone.mes.machine.application.dto.result.MachineOrderExportR;
import com.greenstone.mes.machine.application.dto.result.MachineOrderProgressExportResult;
import com.greenstone.mes.machine.application.dto.result.MachineOrderProgressResult;
import com.greenstone.mes.machine.domain.entity.MachineOrder;
import com.greenstone.mes.machine.infrastructure.mapper.MachineOrderMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineOrderDO;
import com.greenstone.mes.system.api.domain.SysFile;
import com.greenstone.mes.table.core.TableService;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-11-25-10:35
 */
public interface MachineOrderService extends TableService<MachineOrder, MachineOrderDO, MachineOrderMapper> {

    List<MachineOrderProgressResult> selectOrderProgressList(MachineOrderProgressQuery query);

    List<MachineOrderProgressExportResult> selectOrderProgressExportList(MachineOrderProgressQuery query);

    SysFile contractPrint(MachineOrderContractExportQuery query);

    List<MachineOrderExportR> selectExportDataList(MachineOrderExportQuery query);

}
