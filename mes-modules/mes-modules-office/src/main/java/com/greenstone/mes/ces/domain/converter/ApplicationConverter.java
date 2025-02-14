package com.greenstone.mes.ces.domain.converter;

import com.greenstone.mes.ces.domain.entity.CesApplication;
import com.greenstone.mes.ces.domain.entity.CesApplicationItem;
import com.greenstone.mes.ces.infrastructure.persistence.CesApplicationDO;
import com.greenstone.mes.ces.infrastructure.persistence.CesApplicationItemDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/11/23 11:08
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ApplicationConverter {

    // CesApplication
    @Mapping(target = "id", source = "applicationDO.id")
    @Mapping(target = "serialNo", source = "applicationDO.serialNo")
    @Mapping(target = "status", source = "applicationDO.status")
    @Mapping(target = "expectReceiveDate", source = "applicationDO.expectReceiveDate")
    @Mapping(target = "remark", source = "applicationDO.remark")
    @Mapping(target = "items", source = "itemDOs")
    @Mapping(target = "appliedBy", source = "applicationDO.appliedBy")
    @Mapping(target = "appliedByName", source = "applicationDO.appliedByName")
    @Mapping(target = "appliedTime", source = "applicationDO.appliedTime")
    CesApplication toCesApplication(CesApplicationDO applicationDO, List<CesApplicationItemDO> itemDOs);

    CesApplication toCesApplication(CesApplicationDO applicationDO);

    List<CesApplication> toCesApplicationList(List<CesApplicationDO> applicationDOs);

    List<CesApplicationItem> toCesApplicationItems(List<CesApplicationItemDO> itemDOs);

    CesApplicationItem toCesApplicationItem(CesApplicationItemDO itemDO);

    // CesApplication
    CesApplicationDO toCesApplicationDO(CesApplication application);

    // CesApplicationItemDO
    List<CesApplicationItemDO> toCesApplicationItemDOs(List<CesApplicationItem> items);

    CesApplicationItemDO toCesApplicationItemDO(CesApplicationItem item);

}
