package com.greenstone.mes.ces.domain.converter;

import com.greenstone.mes.ces.domain.entity.Requisition;
import com.greenstone.mes.ces.domain.entity.RequisitionItem;
import com.greenstone.mes.ces.infrastructure.persistence.RequisitionDO;
import com.greenstone.mes.ces.infrastructure.persistence.RequisitionItemDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RequisitionConverter {

    Requisition toRequisition(RequisitionDO requisitionDO);

    @Mapping(target = "id", source = "requisitionDO.id")
    @Mapping(target = "serialNo", source = "requisitionDO.serialNo")
    @Mapping(target = "status", source = "requisitionDO.status")
    @Mapping(target = "requisitionDate", source = "requisitionDO.requisitionDate")
    @Mapping(target = "remark", source = "requisitionDO.remark")
    @Mapping(target = "items", source = "itemDOs")
    @Mapping(target = "requisitionerId", source = "requisitionDO.requisitionerId")
    @Mapping(target = "requisitionerName", source = "requisitionDO.requisitionerName")
    @Mapping(target = "requisitionerNo", source = "requisitionDO.requisitionerNo")
    Requisition toRequisition(RequisitionDO requisitionDO, List<RequisitionItemDO> itemDOs);

    RequisitionDO toRequisitionDO(Requisition requisition);

    RequisitionItemDO toRequisitionItemDO(RequisitionItem item);

    List<RequisitionItemDO> toRequisitionItemDOs(List<RequisitionItem> items);

    RequisitionItem toRequisitionItem(RequisitionItemDO itemDO);

    List<RequisitionItem> toRequisitionItemS(List<RequisitionItemDO> itemDOs);
}
