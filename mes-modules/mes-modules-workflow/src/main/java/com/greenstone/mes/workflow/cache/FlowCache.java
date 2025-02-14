package com.greenstone.mes.workflow.cache;

import com.greenstone.mes.workflow.infrastructure.mapper.FlwProcessMapper;
import com.greenstone.mes.workflow.infrastructure.persistence.FlwProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class FlowCache {

    private final FlwProcessMapper flwProcessMapper;

    private volatile List<FlwProcess> processes;

    public List<FlwProcess> getProcess() {
        if (this.processes == null) {
            synchronized (this) {
                if (processes == null) {
                    processes = flwProcessMapper.selectList(null);
                }
            }
        }
        return this.processes;
    }
}
