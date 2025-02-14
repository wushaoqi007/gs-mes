package com.greenstone.mes.form.application.assembler;

import com.greenstone.mes.form.domain.entity.CustomFormDataEntity;
import com.greenstone.mes.form.dto.cmd.ProcessResult;
import com.greenstone.mes.form.dto.cmd.FormDataSaveCmd;
import com.greenstone.mes.form.dto.result.FormCommitResult;
import com.greenstone.mes.form.dto.result.FormDraftResult;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface FormDataAssembler {

    CustomFormDataEntity saveCmd2Entity(FormDataSaveCmd saveCmd);

    CustomFormDataEntity approvedCmd2Entity(ProcessResult processResult);

    FormDraftResult entity2DraftResult(CustomFormDataEntity formDataEntity);

    FormCommitResult entity2CommitResult(CustomFormDataEntity formDataEntity);

}
