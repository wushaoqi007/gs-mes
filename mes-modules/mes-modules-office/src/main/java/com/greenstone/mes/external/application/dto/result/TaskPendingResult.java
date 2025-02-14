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
public class TaskPendingResult {

    private String taskId;
    private String processInstanceId;
    private TaskStatus taskStatus;
    private String serialNo;
    private String billType;
    private Long appliedBy;
    private String appliedByName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appliedTime;

}
