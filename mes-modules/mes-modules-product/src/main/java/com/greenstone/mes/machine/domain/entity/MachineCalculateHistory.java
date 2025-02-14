
package com.greenstone.mes.machine.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MachineCalculateHistory {

    private String id;
    private String calculateDetailId;
    private String calculateSerialNo;
    private Long materialId;
    private String partCode;
    private String partName;
    private String partVersion;
    private Double calculatePrice;
    private String calculateBy;
    private Long calculateById;
    private String calculateJson;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime calculateTime;
}
