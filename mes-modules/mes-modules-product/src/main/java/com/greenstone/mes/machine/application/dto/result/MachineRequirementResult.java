package com.greenstone.mes.machine.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.domain.entity.MachineRequirementDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineRequirementResult {
    private Long id;
    private String serialNo;
    private String spNo;
    private boolean locked;
    private boolean checked;
    private ProcessStatus status;
    private String projectCode;
    private Long applyById;
    private String applyByWxId;
    private String applyBy;
    private String applyByNo;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime applyTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime confirmTime;
    private String confirmBy;
    private String remark;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receiveDeadline;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approveTime;
    private String title;
    private String content;
    private List<Long> approvers;
    private List<Long> copyTo;

    private Integer mailStatus;
    private String mailMsg;

    private List<MachineRequirementDetail> parts;

}
