package com.greenstone.mes.workflow.domain.entity;

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
public class FlwHisTask {

    private Long id;
    private String instanceNo;
    private String serialNo;
    private String processKey;
    private String processName;
    private Long originatorId;
    private String originator;
    private String originatorNo;
    private List<HisTaskNode> taskNodes;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HisTaskNode {
        private Integer taskStatus;
        private LocalDateTime createTime;
        private LocalDateTime finishTime;
        private Long approverId;
        private String approver;
        private String approverNo;
    }

}
