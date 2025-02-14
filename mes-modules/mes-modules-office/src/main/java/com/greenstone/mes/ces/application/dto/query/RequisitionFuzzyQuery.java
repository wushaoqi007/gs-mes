package com.greenstone.mes.ces.application.dto.query;

import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.Data;

import java.util.List;

@Data
public class RequisitionFuzzyQuery {

    private String key;

    private List<String> fields;

    private ProcessStatus status;

    private String toBeReturned;

}
