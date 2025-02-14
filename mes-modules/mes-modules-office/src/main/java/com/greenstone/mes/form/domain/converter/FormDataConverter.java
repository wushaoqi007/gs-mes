package com.greenstone.mes.form.domain.converter;

import com.greenstone.mes.form.domain.entity.CustomFormDataEntity;
import com.greenstone.mes.form.dto.result.BaseFormDataDataResult;
import com.greenstone.mes.form.infrastructure.persistence.FormDataPo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface FormDataConverter {

    FormDataPo entity2Do(CustomFormDataEntity formDataEntity);

    List<BaseFormDataDataResult> do2Results(List<FormDataPo> formDataDoList);

    CustomFormDataEntity do2entity(FormDataPo formDataDo);
}
