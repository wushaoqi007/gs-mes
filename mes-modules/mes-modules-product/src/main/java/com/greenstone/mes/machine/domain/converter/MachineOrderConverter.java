package com.greenstone.mes.machine.domain.converter;

import com.greenstone.mes.machine.domain.entity.MachineOrder;
import com.greenstone.mes.machine.domain.entity.MachineOrderAttachment;
import com.greenstone.mes.machine.domain.entity.MachineOrderDetail;
import com.greenstone.mes.machine.infrastructure.persistence.MachineOrderAttachmentDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineOrderDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineOrderDetailDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-11-23-15:38
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MachineOrderConverter {

    @Mapping(target = "id", source = "machineOrderDO.id")
    @Mapping(target = "serialNo", source = "machineOrderDO.serialNo")
    @Mapping(target = "provider", source = "machineOrderDO.provider")
    @Mapping(target = "remark", source = "machineOrderDO.remark")
    @Mapping(target = "parts", source = "detailDOS")
    MachineOrder toMachineOrder(MachineOrderDO machineOrderDO, List<MachineOrderDetailDO> detailDOS);

    MachineOrderDO entity2Do(MachineOrder machineOrder);

    List<MachineOrderDO> entities2Dos(List<MachineOrder> machineOrders);


    MachineOrder do2Entity(MachineOrderDO machineOrderDO);

    List<MachineOrder> dos2Entities(List<MachineOrderDO> machineOrderDOS);

    // MachineOrderDetail
    MachineOrderDetailDO detailEntity2Do(MachineOrderDetail machineOrderDetail);

    List<MachineOrderDetailDO> detailEntities2Dos(List<MachineOrderDetail> machineOrderDetails);


    MachineOrderDetail detailDo2Entity(MachineOrderDetailDO machineOrderDetailDO);

    List<MachineOrderDetail> detailDos2Entities(List<MachineOrderDetailDO> machineOrderDetailDOS);

    // MachineOrderAttachment
    List<MachineOrderAttachmentDO> attachEntities2Dos(List<MachineOrderAttachment> attachments);

    MachineOrderAttachmentDO attachEntities2Dos(MachineOrderAttachment attachments);

    List<MachineOrderAttachment> attachDos2Entities(List<MachineOrderAttachmentDO> attachDOS);

    MachineOrderAttachment attachDos2Entities(MachineOrderAttachmentDO attachDO);
}
