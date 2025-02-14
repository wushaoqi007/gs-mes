package com.greenstone.mes.form.domain.converter;

import com.greenstone.mes.form.domain.entity.Form;
import com.greenstone.mes.form.domain.entity.FormDefinition;
import com.greenstone.mes.form.infrastructure.persistence.FormDefinitionDo;
import com.greenstone.mes.form.infrastructure.persistence.FormDo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface FormConverter {

    FormDo entity2Do(Form form);

    Form do2Entity(FormDo formDo);

    List<Form> dos2Entities(List<FormDo> formDos);

    FormDefinitionDo formEntity2DefinitionDo(Form form);

}
