package com.greenstone.mes.system.application.service.impl;


import com.greenstone.mes.system.domain.BillParam;
import com.greenstone.mes.system.application.assembler.BillParamAssembler;
import com.greenstone.mes.system.application.service.BillParamService;
import com.greenstone.mes.system.infrastructure.mapper.BillParamMapper;
import com.greenstone.mes.system.infrastructure.po.BillParamDo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BillParamServiceImpl implements BillParamService {

    private final BillParamMapper billParamMapper;
    private final BillParamAssembler assembler;

    @Override
    public void save(BillParamDo billParam) {
        BillParamDo selectParam = BillParamDo.builder().billType(billParam.getBillType())
                .paramKey(billParam.getParamKey()).build();
        BillParamDo existingParam = billParamMapper.getOneOnly(selectParam);
        if (existingParam == null) {
            billParamMapper.insert(billParam);
        } else {
            BillParamDo updateParam = BillParamDo.builder().billType(billParam.getBillType())
                    .id(existingParam.getId())
                    .paramValue(billParam.getParamValue()).build();
            billParamMapper.updateById(updateParam);
        }
    }

    @Override
    public void save(List<BillParamDo> billParams) {
        for (BillParamDo billParam : billParams) {
            save(billParam);
        }
    }

    @Override
    public List<BillParam> list(BillParamDo billParam) {
        return assembler.dos2Entities(billParamMapper.list(billParam));
    }

    @Override
    public Map<String, Object> billParams(String billType) {
        List<BillParamDo> billParam = billParamMapper.list(BillParamDo.builder().billType(billType).build());
        return billParam.stream().collect(Collectors.toMap(BillParamDo::getParamKey, BillParamDo::getParamValue));
    }
}
