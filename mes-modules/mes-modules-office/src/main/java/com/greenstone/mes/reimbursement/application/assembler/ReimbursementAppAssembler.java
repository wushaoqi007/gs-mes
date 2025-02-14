package com.greenstone.mes.reimbursement.application.assembler;

import com.greenstone.mes.office.application.assembler.BaseAssembler;
import com.greenstone.mes.reimbursement.application.dto.ReimbursementAppSaveCmd;
import com.greenstone.mes.reimbursement.application.dto.result.ReimbursementAppResult;
import com.greenstone.mes.reimbursement.domain.entity.ReimbursementApplication;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ReimbursementAppAssembler extends BaseAssembler<ReimbursementAppSaveCmd> {
    // ReimbursementApplication
    ReimbursementAppResult toReimbursementAppResult(ReimbursementApplication application);

    List<ReimbursementAppResult> toReimbursementAppResults(List<ReimbursementApplication> applications);

    ReimbursementApplication fromReimbursementAppSaveCmd(ReimbursementAppSaveCmd saveCmd);

}
