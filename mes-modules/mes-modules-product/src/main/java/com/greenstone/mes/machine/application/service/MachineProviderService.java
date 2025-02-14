package com.greenstone.mes.machine.application.service;

import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineProviderAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineProviderImportCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.domain.entity.MachineProvider;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-05-22-9:57
 */
public interface MachineProviderService {
    List<MachineProvider> list(MachineFuzzyQuery fuzzyQuery);

    MachineProvider detail(String id);

    void add(MachineProviderAddCmd addCmd);

    void edit(MachineProviderAddCmd addCmd);

    void importProviders(List<MachineProviderImportCmd> importCmd);

    void delete(List<String> ids);
}
