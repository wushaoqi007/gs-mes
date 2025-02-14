package com.greenstone.mes.machine.infrastructure.mapper;

import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderExportQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderPartListQuery;
import com.greenstone.mes.machine.application.dto.result.MachineOrderExportR;
import com.greenstone.mes.machine.application.dto.result.MachineOrderPartR;
import com.greenstone.mes.machine.infrastructure.persistence.MachineOrderDO;
import com.greenstone.mes.table.infrastructure.config.mubatisplus.TableBaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-11-23-15:27
 */
@Repository
public interface MachineOrderMapper extends TableBaseMapper<MachineOrderDO> {

    List<MachineOrderPartR> selectPartList(MachineOrderPartListQuery query);

    List<MachineOrderExportR> selectExportDataList(MachineOrderExportQuery query);

}
