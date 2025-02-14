package com.greenstone.mes.external.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.external.infrastructure.enums.CopyHandleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProcessCopyResult {

    private String id;
    private String serialNo;
    private Long userId;
    private String formId;
    private CopyHandleStatus handleStatus;
    private Long appliedBy;
    private String appliedByName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appliedTime;
    private ProcessStatus processStatus;
}
