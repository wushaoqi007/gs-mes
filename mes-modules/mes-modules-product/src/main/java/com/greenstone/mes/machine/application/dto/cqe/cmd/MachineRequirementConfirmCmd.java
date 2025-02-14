package com.greenstone.mes.machine.application.dto.cqe.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-11-23-15:48
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineRequirementConfirmCmd {
    @NotEmpty(message = "需求单号不为空")
    private String requirementSerialNo;
    @NotEmpty(message = "需求单id不为空")
    private String requirementId;
    @NotEmpty(message = "请添加零件")
    @Valid
    private List<Part> parts;

    @Data
    public static class Part {
        @NotEmpty(message = "需求单详情id不为空")
        private String requirementDetailId;
        private String partType;
        private Boolean urgent;
        private String provider;
        private LocalDate processDeadline;
        private LocalDate planDeadline;
        private Integer paperNum;
        private Integer scannedPaperNumber;
    }
}
