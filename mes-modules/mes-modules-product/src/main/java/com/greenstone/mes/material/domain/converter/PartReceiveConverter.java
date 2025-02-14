package com.greenstone.mes.material.domain.converter;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.material.domain.entity.PartReceive;
import com.greenstone.mes.material.domain.entity.PartReceiveRecord;
import com.greenstone.mes.material.infrastructure.persistence.PartReceiveDO;
import com.greenstone.mes.material.infrastructure.persistence.PartReceiveRecordDO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {StrUtil.class}
)
public interface PartReceiveConverter {

    PartReceiveDO toPartReceiveDO(PartReceive partReceive);

    List<PartReceiveDO> toPartReceiveDOs(List<PartReceive> partReceives);


    PartReceive toPartReceive(PartReceiveDO partReceiveDO);

    List<PartReceive> toPartReceives(List<PartReceiveDO> partReceiveDOs);

    PartReceiveRecord toPartReceiveRecord(PartReceiveRecordDO partReceiveRecordDO);

    List<PartReceiveRecord> toPartReceiveRecords(List<PartReceiveRecordDO> partReceiveRecordDOS);
}
