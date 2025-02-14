package com.greenstone.mes.external.domain.entity;

import com.greenstone.mes.external.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author gu_renkai
 * @date 2023/3/2 9:04
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessTask {

    private String taskId;
    private String processInstanceId;
    private TaskStatus taskStatus;
    private String comment;
    private String serialNo;
    private String formId;
    private String formName;
    private Long appliedBy;
    private String appliedByName;
    private LocalDateTime appliedTime;
    private Long approvedBy;
    private String approvedByName;
    private LocalDateTime approvedTime;

}
