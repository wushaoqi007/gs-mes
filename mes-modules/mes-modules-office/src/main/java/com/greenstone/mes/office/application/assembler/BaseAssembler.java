package com.greenstone.mes.office.application.assembler;

import com.greenstone.mes.form.dto.cmd.FormDataSaveCmd;
import com.greenstone.mes.reimbursement.domain.entity.ReimbursementApplication;

public interface BaseAssembler<SC extends FormDataSaveCmd> {

    default ReimbursementApplication fromReimbursementAppSaveCmd(SC o) {
        return fromReimbursementAppSaveCmd((SC) o);
    }
}
