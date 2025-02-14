package com.greenstone.mes.machine.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.domain.converter.MachineProviderConverter;
import com.greenstone.mes.machine.domain.entity.MachineProvider;
import com.greenstone.mes.machine.infrastructure.mapper.MachineProviderMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineProviderDO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-05-22-9:06
 */
@Slf4j
@AllArgsConstructor
@Service
public class MachineProviderRepository {
    private final MachineProviderMapper providerMapper;
    private final MachineProviderConverter providerConverter;

    public List<MachineProvider> list(MachineFuzzyQuery fuzzyQuery) {
        QueryWrapper<MachineProviderDO> fuzzyQueryWrapper = new QueryWrapper<>();
        fuzzyQueryWrapper.orderByDesc("create_time");
        if (StrUtil.isNotEmpty(fuzzyQuery.getKey()) && CollUtil.isNotEmpty(fuzzyQuery.getFields())) {
            fuzzyQueryWrapper.and(wrapper -> {
                for (String field : fuzzyQuery.getFields()) {
                    String fieldCode = StrUtil.toSymbolCase(field, '_');
                    wrapper.or().like(fieldCode, fuzzyQuery.getKey());
                }
            });
        }
        List<MachineProviderDO> machineProviderDOS = providerMapper.selectList(fuzzyQueryWrapper);
        return providerConverter.dos2Entities(machineProviderDOS);
    }

    public MachineProvider findByName(String name) {
        MachineProviderDO oneOnly = providerMapper.getOneOnly(MachineProviderDO.builder().name(name).build());
        if (oneOnly == null) {
            throw new ServiceException(StrUtil.format("未找到供应商:{}", name));
        }
        return providerConverter.do2Entity(oneOnly);
    }

    public MachineProvider findByFullName(String fullName) {
        MachineProviderDO oneOnly = providerMapper.getOneOnly(MachineProviderDO.builder().fullName(fullName).build());
        if (oneOnly == null) {
            throw new ServiceException(StrUtil.format("未找到供应商:{}", fullName));
        }
        return providerConverter.do2Entity(oneOnly);
    }

    public MachineProvider detail(String id) {
        MachineProviderDO machineProviderDO = providerMapper.selectById(id);
        if (machineProviderDO == null) {
            throw new ServiceException(StrUtil.format("未找到供应商，id:{}", id));
        }
        return providerConverter.do2Entity(machineProviderDO);
    }

    public void add(MachineProvider provider) {
        MachineProviderDO saveDO = providerConverter.entity2Do(provider);
        // 重名校验
        MachineProviderDO getByName = providerMapper.getOneOnly(MachineProviderDO.builder().fullName(provider.getFullName()).build());
        if (getByName != null) {
            throw new ServiceException(StrUtil.format("已存在同名供应商：{}", provider.getFullName()));
        }
        providerMapper.insert(saveDO);
    }

    public void importProviders(List<MachineProvider> providers) {
        List<MachineProviderDO> insertDos = new ArrayList<>();
        List<MachineProviderDO> saveDOs = providerConverter.entities2Dos(providers);
        for (MachineProviderDO saveDO : saveDOs) {
            MachineProviderDO getByName = providerMapper.getOneOnly(MachineProviderDO.builder().fullName(saveDO.getFullName()).build());
            if (getByName != null) {
                // 导入时，重名的直接更新信息
                saveDO.setId(getByName.getId());
                providerMapper.updateById(saveDO);
            } else {
                insertDos.add(saveDO);
            }
        }
        if (CollUtil.isNotEmpty(insertDos)) {
            providerMapper.insertBatchSomeColumn(insertDos);
        }
    }

    public void edit(MachineProvider provider) {
        MachineProviderDO saveDO = providerConverter.entity2Do(provider);
        MachineProviderDO find = providerMapper.selectById(provider.getId());
        if (find == null) {
            throw new ServiceException(StrUtil.format("未找到供应商，id:{}", provider.getId()));
        }
        // 重名校验
        MachineProviderDO getByName = providerMapper.getOneOnly(MachineProviderDO.builder().fullName(provider.getFullName()).build());
        if (getByName != null && !getByName.getId().equals(provider.getId())) {
            throw new ServiceException(StrUtil.format("已存在同名供应商：{}", provider.getFullName()));
        }
        providerMapper.updateById(saveDO);
    }

    public void delete(List<String> ids) {
        LambdaQueryWrapper<MachineProviderDO> removeWrapper = Wrappers.lambdaQuery(MachineProviderDO.class).in(MachineProviderDO::getId, ids);
        providerMapper.delete(removeWrapper);
    }
}
