package com.greenstone.mes.workflow.resp;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlowCommitResp {

    // 流程实例编号
    private String instanceNo;

}
