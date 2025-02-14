package com.greenstone.mes.table.domain.converter;

import com.greenstone.mes.table.domain.entity.TestEntity;
import com.greenstone.mes.table.infrastructure.persistence.TestPo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TestConverter {

    TestEntity toTest(TestPo po);

    List<TestEntity> toTests(List<TestPo> pos);

    TestPo toTestPo(TestEntity test);

    List<TestPo> toTestPos(List<TestEntity> domains);

}
