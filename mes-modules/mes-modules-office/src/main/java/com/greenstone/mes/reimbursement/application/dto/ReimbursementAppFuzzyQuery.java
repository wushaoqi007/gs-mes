package com.greenstone.mes.reimbursement.application.dto;

import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.Data;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/21 15:11
 */

@Data
public class ReimbursementAppFuzzyQuery {

    private String key;

    private List<String> fields;

    private ProcessStatus status;

    private Long submitById;

    private Long approvedById;

}
