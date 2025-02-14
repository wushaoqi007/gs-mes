package com.greenstone.mes.system.domain.converter;

import com.greenstone.mes.system.domain.entity.SysDept2;
import com.greenstone.mes.system.infrastructure.po.DeptPo;
import org.mapstruct.Mapper;
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
public interface DeptConverter {

    SysDept2 do2Entity(DeptPo deptDo);

    List<SysDept2> do2Entities(List<DeptPo> deptDos);
}
