package com.greenstone.mes.external.application.service;

import com.greenstone.mes.external.application.dto.cmd.CopyAddCmd;
import com.greenstone.mes.external.application.dto.cmd.CopyStatusChangeCmd;
import com.greenstone.mes.external.application.dto.query.TaskQ;
import com.greenstone.mes.external.application.dto.result.ProcessCopyResult;
import com.greenstone.mes.external.application.dto.result.TaskResult;
import com.greenstone.mes.external.dto.cmd.ProcessCmd;
import com.greenstone.mes.external.dto.cmd.TaskRunCmd;
import com.greenstone.mes.external.dto.result.TaskRunResult;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/3/2 13:07
 */

public interface ProcessTaskService {

    List<TaskResult> taskList(TaskQ taskQ);

    TaskRunResult runTask(ProcessCmd runCmd);

    TaskRunResult runTask(TaskRunCmd runCmd);

    List<TaskResult> pendingTasks();

    List<TaskResult> processedTasks();

    void addCopy(CopyAddCmd copyAddCmd);

    List<ProcessCopyResult> currUserCopies();

    void copyHandleStatusChange(CopyStatusChangeCmd changeCmd);

    void revokeTask(String processInstanceId);
}
