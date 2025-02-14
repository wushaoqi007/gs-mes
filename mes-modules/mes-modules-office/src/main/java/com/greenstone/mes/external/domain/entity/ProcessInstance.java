package com.greenstone.mes.external.domain.entity;

import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author gu_renkai
 * @date 2023/3/2 10:30
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessInstance {

    private String processInstanceId;
    private ProcessStatus processStatus;
    private String serialNo;
    private String formId;
    private String formName;
    private String serviceName;
    private Long appliedBy;
    private String appliedByName;
    private LocalDateTime appliedTime;


}
