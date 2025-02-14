package com.greenstone.mes.external.application.delegate;

import com.greenstone.mes.common.core.utils.SpringUtils;
import com.greenstone.mes.external.application.dto.cmd.CopyAddCmd;
import com.greenstone.mes.external.application.service.ProcessTaskService;

/**
 * 流程抄送功能实现
 * 此类在 flowable 的流程定义中使用，类名称在 idea 置灰并不是没有被用到，勿删
 */
//public class CopyDelegate implements JavaDelegate {

//    @Override
//    public void execute(DelegateExecution execution) {
//        ProcessTaskService processTaskService = SpringUtils.getBean(ProcessTaskService.class);
//        CopyAddCmd copyAddCmd = CopyAddCmd.builder().nodeId(execution.getCurrentActivityId())
//                .processInstanceId(execution.getProcessInstanceId())
//                .processDefinitionId(execution.getProcessDefinitionId()).build();
//        processTaskService.addCopy(copyAddCmd);
//    }

//}
