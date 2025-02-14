package com.greenstone.mes.machine.application.dto.cqe.query;

import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.Data;

import java.util.List;

@Data
public class MachineFuzzyQuery {

    private String key;

    private List<String> fields;

    private ProcessStatus status;

    private String applyByWxId;

    private String applyById;

}
