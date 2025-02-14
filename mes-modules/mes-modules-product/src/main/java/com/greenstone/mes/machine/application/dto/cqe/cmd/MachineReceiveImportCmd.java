package com.greenstone.mes.machine.application.dto.cqe.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineReceiveImportCmd {
    @Valid
    @NotEmpty(message = "无法导入空数据")
    private List<Part> parts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        private String serialNo;
        private String provider;
        private LocalDateTime receiveTime;
        private String projectCode;
        private String partCode;
        private String partName;
        private String partVersion;
        private Long expectedNumber;
        private Long actualNumber;
    }

}
