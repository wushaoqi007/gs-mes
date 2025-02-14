package com.greenstone.mes.ces.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class CesApplicationWaitHandleResult {

    private String id;
    private String serialNo;
    private ProcessStatus status;
    private Long appliedBy;
    private String appliedByName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime appliedTime;
    private String billType;
    private String remark;

}
