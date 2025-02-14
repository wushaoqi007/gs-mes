package com.greenstone.mes.machine.domain.repository;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.enums.MachineError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderPartListQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderPartScanQuery;
import com.greenstone.mes.machine.application.dto.result.MachineOrderPartR;
import com.greenstone.mes.machine.domain.converter.MachineOrderConverter;
import com.greenstone.mes.machine.domain.entity.MachineOrder;
import com.greenstone.mes.machine.domain.entity.MachineOrderDetail;
import com.greenstone.mes.machine.infrastructure.mapper.MachineOrderDetailMapper;
import com.greenstone.mes.machine.infrastructure.mapper.MachineOrderMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineOrderDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineOrderDetailDO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author wushaoqi
 * @date 2023-11-24-10:27
 */
@Slf4j
@AllArgsConstructor
@Service
public class MachineOrderOldRepository {
    private final MachineOrderMapper orderMapper;
    private final MachineOrderDetailMapper orderDetailMapper;
    private final MachineOrderConverter orderConverter;

    public MachineOrder selectBySerialNo(String serialNo) {
        MachineOrderDO oneOnly = orderMapper.getOneOnly(MachineOrderDO.builder().serialNo(serialNo).build());
        return orderConverter.do2Entity(oneOnly);
    }

    public MachineOrder detail(String serialNo) {
        MachineOrderDO orderDO = orderMapper.getOneOnly(MachineOrderDO.builder().serialNo(serialNo).build());
        if (orderDO == null) {
            throw new ServiceException(StrUtil.format("订单不存在：{}", serialNo));
        }
        List<MachineOrderDetailDO> detailDOS = orderDetailMapper.list(MachineOrderDetailDO.builder().serialNo(serialNo).build());
        return orderConverter.toMachineOrder(orderDO, detailDOS);
    }

    public List<MachineOrderPartR> selectPartList(MachineOrderPartListQuery query) {
        return orderMapper.selectPartList(query);
    }

    public MachineOrderDetail selectPart(MachineOrderPartScanQuery query) {
        log.info("查询订单零件，参数：{}", query);
        MachineOrderDetailDO detailDO = orderDetailMapper.getOneOnly(MachineOrderDetailDO.builder()
                .serialNo(query.getSerialNo()).requirementSerialNo(query.getRequirementSerialNo()).projectCode(query.getProjectCode())
                .partCode(query.getPartCode()).partVersion(query.getPartVersion()).build());
        if (Objects.isNull(detailDO)) {
            throw new ServiceException(MachineError.E200108, StrUtil.format("零件号/版本：{}/{}", query.getPartCode(), query.getPartVersion()));
        }
        return orderConverter.detailDo2Entity(detailDO);
    }

    public void update(MachineOrderDetail orderDetail) {
        MachineOrderDetailDO machineOrderDetailDO = orderConverter.detailEntity2Do(orderDetail);
        orderDetailMapper.updateById(machineOrderDetailDO);
    }

}
