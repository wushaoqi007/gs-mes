package com.greenstone.mes.machine.application.dto.cqe.cmd;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineCalculateAddCmd {
    private String id;
    private String serialNo;
    private ProcessStatus status;
    private String calculateBy;
    private Long calculateById;
    private LocalDateTime applyTime;
    private LocalDateTime confirmTime;
    private String confirmBy;
    @NotEmpty(message = "请添加零件")
    @Valid
    private List<Part> parts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        private String id;
        private String serialNo;
        private String requirementSerialNo;
        private String projectCode;
        private Long materialId;
        private String partCode;
        private String partName;
        private String partVersion;
        private Long partNumber;

        private Double totalPrice;
        private Double calculatePrice;
        private String calculateJson;
        private LocalDateTime calculateTime;

        public void trim() {
            List<String> trims = List.of(" ", "-", "_");
            partCode = StrUtil.trim(partCode, 0, character -> trims.contains(String.valueOf(character)));
            partCode = partCode.replaceAll("\r", "");
            partCode = partCode.replaceAll("\n", "");
            partName = StrUtil.trim(partName, 0, character -> trims.contains(String.valueOf(character)));
            partName = partName.replaceAll("\r", "");
            partName = partName.replaceAll("\n", "");
            partVersion = StrUtil.trim(partVersion, 0, character -> trims.contains(String.valueOf(character)));
            partVersion = partVersion.replaceAll("\r", "");
            partVersion = partVersion.replaceAll("\n", "");
        }
    }

    public void trim() {
        for (Part part : this.parts) {
            part.trim();
        }
    }

}
