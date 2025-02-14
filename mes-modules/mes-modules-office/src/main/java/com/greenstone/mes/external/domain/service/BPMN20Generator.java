package com.greenstone.mes.external.domain.service;

import com.greenstone.mes.external.domain.entity.node.FlowNode;

/**
 * @author gu_renkai
 * @date 2023/2/24 16:02
 */

public interface BPMN20Generator {

    String generateXml(String flowCode, String flowName, FlowNode flowNode);

}
