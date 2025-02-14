package com.greenstone.mes.machine.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.domain.entity.MachineOrderDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineOrderResult {
    private String id;
    private String serialNo;
    private String contractNo;
    private ProcessStatus status;
    private String provider;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate orderTime;
    private Double totalPrice;
    private String remark;
    private Long totalProcess;
    private Long totalReceived;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private Long createById;
    private String createBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submitTime;
    private String submitBy;
    private Long submitById;
    private List<MachineOrderDetail> parts;
}
