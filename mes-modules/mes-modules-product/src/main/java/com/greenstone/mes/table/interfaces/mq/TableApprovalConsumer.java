package com.greenstone.mes.table.interfaces.mq;

import com.greenstone.mes.common.core.utils.SpringUtils;
import com.greenstone.mes.mq.consts.MqConst;
import com.greenstone.mes.table.TableEntity;
import com.greenstone.mes.table.TablePo;
import com.greenstone.mes.table.core.FunctionModel;
import com.greenstone.mes.table.core.FunctionServiceHelper;
import com.greenstone.mes.table.core.TableThreadLocal;
import com.greenstone.mes.workflow.mq.ApprovalChangeMsg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TableApprovalConsumer<E extends TableEntity, P extends TablePo> {

    private final FunctionServiceHelper<E, P> serviceHelper;

    @KafkaListener(topics = MqConst.Topic.FLOW_APPROVAL_CHANGE, groupId = MqConst.Group.TABLE)
    public void onMessage(ApprovalChangeMsg approvalChangeMsg) {
        log.info("Receive msg, topic: {}, content: {}", MqConst.Topic.FLOW_APPROVAL_CHANGE, approvalChangeMsg);
        FunctionModel<E, P> model = serviceHelper.getService(approvalChangeMsg.getBusinessKey());
        if (model != null) {
            SpringUtils.getBean(TableThreadLocal.class).set(model);
            model.getTableService().updateApprovalChange(approvalChangeMsg);
        }
    }
}
