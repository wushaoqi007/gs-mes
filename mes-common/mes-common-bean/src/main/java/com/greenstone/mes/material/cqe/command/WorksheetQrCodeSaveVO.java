package com.greenstone.mes.material.cqe.command;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Slf4j
public class WorksheetQrCodeSaveVO {

    @NotEmpty(message = "缺少机加工单号")
    private String partOrderCode;

    @NotEmpty(message = "缺少项目代码")
    private String projectCode;

    @NotEmpty(message = "缺少组件名称")
    private String componentCodeName;

    @NotEmpty(message = "缺少零件名称")
    private String partCodeName;

    @NotEmpty(message = "缺少零件版本")
    private String partVersion;

    @NotNull(message = "缺少零件数量")
    private Long partNumber;

    @NotNull(message = "缺少图纸张数")
    private Integer paperNumber;

    @NotEmpty(message = "缺少重量")
    private String weight;

    @NotEmpty(message = "缺少原材料")
    private String rawMaterial;

    private String surfaceTreatment;

    @NotEmpty(message = "缺少设计姓名")
    private String designer;

    @NotNull(message = "购买原因不能为空")
    private Integer purchaseReason;

    @NotNull(message = "公司类型不能为空")
    private Integer companyType;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public String getComponentCode() {
        if (companyType == 1) {
            return projectCode + "-" + componentCodeName.substring(0, 2);
        } else {
            return componentCodeName;
        }
    }

    public String getComponentName() {
        if (companyType == 1) {
            return componentCodeName.substring(2);
        } else {
            return componentCodeName;
        }

    }

    public String getPartCode() {
        String[] codeName = partCodeName.split(" ");
        return codeName[0];
    }

    public String getPartName() {
        log.info("partCodeName {}", partCodeName);
        String[] codeName = partCodeName.split(" ");
        String nameVersion = partCodeName.substring(codeName[0].length() + 1);
        if (nameVersion.length() > 2) {
            char lastSecondChar = nameVersion.charAt(nameVersion.length() - 2);
            if (lastSecondChar == 'V' || lastSecondChar == 'v') {
                return nameVersion.substring(0, nameVersion.length() - 2);
            }
        }
        return nameVersion;
    }

}
