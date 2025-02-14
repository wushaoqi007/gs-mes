package com.greenstone.mes.machine.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineOrderPriceImportCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderProgressQuery;
import com.greenstone.mes.machine.application.dto.result.MachineOrderProgressResult;
import com.greenstone.mes.machine.domain.entity.MachineOrderDetail;
import com.greenstone.mes.machine.infrastructure.persistence.MachineOrderDetailDO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-11-23-15:27
 */
@Repository
public interface MachineOrderDetailMapper extends EasyBaseMapper<MachineOrderDetailDO> {

    List<MachineOrderProgressResult> selectOrderDetailList(MachineOrderProgressQuery query);

    List<MachineOrderDetail> selectEffectiveParts(String requirementSerialNo);

    MachineOrderDetail selectEffectivePart(MachineOrderDetail orderDetail);
}
