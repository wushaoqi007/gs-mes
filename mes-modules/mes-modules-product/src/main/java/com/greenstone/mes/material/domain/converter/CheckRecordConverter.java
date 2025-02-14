package com.greenstone.mes.material.domain.converter;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.material.domain.entity.CheckRecord;
import com.greenstone.mes.material.infrastructure.persistence.CheckRecordDO;
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
public interface CheckRecordConverter {

    CheckRecordDO toCheckRecordDO(CheckRecord checkRecord);

    List<CheckRecordDO> toCheckRecordDOs(List<CheckRecord> checkRecords);


    CheckRecord toCheckRecord(CheckRecordDO checkRecordDO);
    
    List<CheckRecord> toCheckRecords(List<CheckRecordDO> checkRecordDOs);

}
