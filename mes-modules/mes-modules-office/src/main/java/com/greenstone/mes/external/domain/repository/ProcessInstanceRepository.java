package com.greenstone.mes.external.domain.repository;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.external.domain.converter.ProcessConverter;
import com.greenstone.mes.external.domain.entity.ProcessInstance;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.external.infrastructure.mapper.ProcessInstanceMapper;
import com.greenstone.mes.external.infrastructure.persistence.ProcessInstanceDO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author gu_renkai
 * @date 2023/3/2 13:16
 */
@AllArgsConstructor
@Service
public class ProcessInstanceRepository {

    private ProcessInstanceMapper processInstanceMapper;
    private ProcessConverter converter;

    public ProcessInstance get(String processInstanceId) {
        ProcessInstanceDO procInstDO = processInstanceMapper.getOneOnly(ProcessInstanceDO.builder().processInstanceId(processInstanceId).build());
        return converter.toBillProcInst(procInstDO);
    }

    public ProcessInstance getBySn(String serialNo) {
        ProcessInstanceDO procInstDO = processInstanceMapper.getOneOnly(ProcessInstanceDO.builder().serialNo(serialNo).build());
        return converter.toBillProcInst(procInstDO);
    }

    public void add(ProcessInstance processInstance) {
        processInstanceMapper.insert(converter.toBillProcInstDO(processInstance));
    }

    public void changeStatus(ProcessInstance processInstance) {
        processInstanceMapper.updateById(converter.toBillProcInstDO(processInstance));
    }

    public void revoke(String processInstanceId) {
        LambdaUpdateWrapper<ProcessInstanceDO> updateWrapper = Wrappers.lambdaUpdate(ProcessInstanceDO.class)
                .set(ProcessInstanceDO::getProcessStatus, ProcessStatus.REVOKED)
                .eq(ProcessInstanceDO::getProcessInstanceId, processInstanceId);
        processInstanceMapper.update(updateWrapper);
    }

}
