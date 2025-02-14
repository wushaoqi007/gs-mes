package com.greenstone.mes.external.domain.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.external.domain.converter.ProcessConverter;
import com.greenstone.mes.external.domain.entity.ProcessDefinition;
import com.greenstone.mes.external.infrastructure.mapper.HiProcessDefinitionMapper;
import com.greenstone.mes.external.infrastructure.mapper.ProcessDefinitionMapper;
import com.greenstone.mes.external.infrastructure.persistence.HiProcessDefinitionDo;
import com.greenstone.mes.external.infrastructure.persistence.ProcessDefinitionDO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author gu_renkai
 * @date 2023/3/1 13:13
 */
@AllArgsConstructor
@Service
public class ProcessDefinitionRepository {

    private final ProcessDefinitionMapper processDefinitionMapper;
    private final HiProcessDefinitionMapper hiProcessDefinitionMapper;
    private final ProcessConverter converter;

    public ProcessDefinition save(ProcessDefinition procDef) {
        // 保存当前的表单流程定义
        ProcessDefinitionDO billProcdefDO = converter.toProcessDefinitionDO(procDef);
        ProcessDefinitionDO existBillProcDef =
                processDefinitionMapper.getOneOnly(ProcessDefinitionDO.builder().formId(procDef.getFormId()).build());
        if (existBillProcDef == null) {
            processDefinitionMapper.insert(billProcdefDO);
        } else {
            billProcdefDO.setId(existBillProcDef.getId());
            processDefinitionMapper.updateById(billProcdefDO);
        }
        // 保存表单流程定义的历史数据
        HiProcessDefinitionDo hiProcessDefinitionDO = converter.toHiProcessDefinitionDO(procDef);
        hiProcessDefinitionMapper.insert(hiProcessDefinitionDO);
        return converter.toProcessDefinition(billProcdefDO);
    }

    public ProcessDefinition get(String formId) {
        ProcessDefinitionDO existProcBillType = processDefinitionMapper.getOneOnly(ProcessDefinitionDO.builder().formId(formId).build());
        return converter.toProcessDefinition(existProcBillType);
    }

    public Boolean isDefinitionExist(String formId) {
        LambdaQueryWrapper<ProcessDefinitionDO> queryWrapper = Wrappers.lambdaQuery(ProcessDefinitionDO.class)
                .select(ProcessDefinitionDO::getProcessDefinitionId).eq(ProcessDefinitionDO::getFormId, formId);
        return processDefinitionMapper.exists(queryWrapper);
    }

}
