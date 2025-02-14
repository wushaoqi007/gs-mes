package com.greenstone.mes.ces.domain.entity;

import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/21 15:11
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CesApplication {

    private String id;
    private String serialNo;
    private ProcessStatus status;
    private LocalDate expectReceiveDate;
    private String remark;
    private Long appliedBy;
    private String appliedByName;
    private LocalDateTime appliedTime;
    private List<CesApplicationItem> items;

}
