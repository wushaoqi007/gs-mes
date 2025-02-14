package com.greenstone.mes.workflow.mq;

import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.system.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalChangeMsg {

    /**
     * 详细id
     */
    private Long itemId;

    /**
     * 单号
     */
    private String serialNo;

    /**
     * 流程实例id
     */
    private String instanceNo;

    /**
     * 实例状态
     */
    private ProcessStatus status;

    /**
     * 业务关键字（功能id）
     */
    private String businessKey;

    /**
     * 流程名称
     */
    private String processName;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 操作人
     */
    private User operator;

    /**
     * 审批意见
     */
    private String remark;
}
