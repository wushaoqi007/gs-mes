package com.greenstone.mes.form.dto.result;

import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormDraftResult {

    private String id;
    private String serialNo;
    private ProcessStatus status;

}
