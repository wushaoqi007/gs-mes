package com.greenstone.mes.external.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.external.enums.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author gu_renkai
 * @date 2023/3/3 14:31
 */
@Data
public class TaskResult {

    private String taskId;
    private String processInstanceId;
    private TaskStatus taskStatus;
    private String comment;
    private String serialNo;
    private String formId;
    private String formName;
    private Long appliedBy;
    private String appliedByName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appliedTime;
    private Long approvedBy;
    private String approvedByName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvedTime;

}
