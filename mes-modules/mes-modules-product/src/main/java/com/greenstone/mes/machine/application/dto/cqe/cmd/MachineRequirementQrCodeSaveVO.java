package com.greenstone.mes.machine.application.dto.cqe.cmd;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author wushaoqi
 * @date 2023-11-23-15:48
 */
@Data
public class MachineRequirementQrCodeSaveVO {
    @NotEmpty(message = "机加工单号信息不能为空")
    private String serialNo;
    @NotEmpty(message = "项目代码不为空")
    private String projectCode;
    private Long materialId;
    @NotEmpty(message = "零件号不为空")
    private String materialCode;
    @NotEmpty(message = "零件名称不为空")
    private String materialName;
    @NotEmpty(message = "零件版本不为空")
    private String version;
    @NotNull(message = "单套数量不为空")
    private Long perSet;
    @NotNull(message = "套数不为空")
    private Integer setsNumber;
    private Long processNumber;
    @NotNull(message = "图纸数量不为空")
    private Integer paperNumber;
    private String surfaceTreatment;
    private String rawMaterial;
    private String weight;
    private LocalDateTime printDate;
    private String hierarchy;
    @NotEmpty(message = "设计不为空")
    private String designer;
    private String remark;
    private String applyByWxId;

    public Long calProcessNumber() {
        if (Objects.nonNull(this.processNumber)) {
            return processNumber;
        } else {
            return this.perSet * setsNumber.longValue();
        }
    }

}
