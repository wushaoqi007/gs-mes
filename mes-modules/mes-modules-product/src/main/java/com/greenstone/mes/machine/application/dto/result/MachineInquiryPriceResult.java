package com.greenstone.mes.machine.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.domain.entity.MachineInquiryPriceDetail;
import com.greenstone.mes.machine.infrastructure.enums.InquiryPriceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MachineInquiryPriceResult {

    private String id;
    private String serialNo;
    private ProcessStatus status;
    private InquiryPriceStatus handleStatus;
    private Integer categoryTotal;
    private Long partTotal;
    private Integer paperTotal;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submitTime;
    private String submitBy;
    private Long submitById;
    private Boolean urgent;
    private String remark;
    private String createBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private List<MachineInquiryPriceDetail> parts;
}
