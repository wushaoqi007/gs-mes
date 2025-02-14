package com.greenstone.mes.external.application.service;

import com.greenstone.mes.external.domain.entity.node.FlowNode;
import org.springframework.stereotype.Repository;

/**
 * @author gu_renkai
 * @date 2023/3/2 13:59
 */
@Repository
public interface ProcessNodeDefService {

    void save(FlowNode flowNode, String billTypeId, String procdefId);

}
