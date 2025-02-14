package com.greenstone.mes.machine.application.service.impl;

import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.machine.application.assemble.MachineProviderAssemble;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineProviderAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineProviderImportCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.service.MachineProviderService;
import com.greenstone.mes.machine.domain.entity.MachineProvider;
import com.greenstone.mes.machine.domain.repository.MachineProviderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-05-22-13:43
 */
@AllArgsConstructor
@Slf4j
@Service
public class MachineProviderServiceImpl implements MachineProviderService {

    private final MachineProviderRepository providerRepository;
    private final MachineProviderAssemble providerAssemble;

    @Override
    public List<MachineProvider> list(MachineFuzzyQuery fuzzyQuery) {
        log.info("provider list query:{}", fuzzyQuery);
        return providerRepository.list(fuzzyQuery);
    }

    @Override
    public MachineProvider detail(String id) {
        log.info("provider detail query:{}", id);
        return providerRepository.detail(id);
    }

    @Override
    public void add(MachineProviderAddCmd addCmd) {
        log.info("provider add:{}", addCmd);
        MachineProvider machineProvider = providerAssemble.toMachineProvider(addCmd);
        providerRepository.add(machineProvider);
    }

    @Override
    public void edit(MachineProviderAddCmd addCmd) {
        log.info("provider edit:{}", addCmd);
        if (addCmd.getId() == null) {
            throw new ServiceException("id不为空");
        }
        MachineProvider machineProvider = providerAssemble.toMachineProvider(addCmd);
        providerRepository.edit(machineProvider);
    }

    @Override
    public void importProviders(List<MachineProviderImportCmd> importCmd) {
        log.info("provider import:{}", importCmd);
        List<MachineProvider> machineProviders = providerAssemble.toMachineProviderFromImportS(importCmd);
        providerRepository.importProviders(machineProviders);
    }

    @Override
    public void delete(List<String> ids) {
        log.info("provider delete:{}", ids);
        providerRepository.delete(ids);
    }
}
