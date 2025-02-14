package com.greenstone.mes.material.request;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.greenstone.mes.common.core.annotation.Excel;
import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 机加工单导入(二厂)
 */
@Data
@Valid
public class ProcessOrderF2ImportVO {

    @Excel(name = "序号")
    @NotEmpty(message = "序号不能为空")
    private String seqNum;

    @Excel(name = "料号")
    private String materialNumber;

    @Excel(name = "名称")
    @NotEmpty(message = "名称不能为空")
    private String partName;

    @Excel(name = "图号")
    @NotEmpty(message = "图号不能为空")
    private String paperCode;

    @Excel(name = "材质")
    private String rawMaterial;

    @Excel(name = "表面处理")
    private String surfaceTreatment;

    @Excel(name = "热处理")
    private String hotTreatment;

    @Excel(name = "加工类型")
    private String type;

    @Excel(name = "单位")
    private String unit;

    @Excel(name = "数量")
    @NotNull(message = "数量不能为空")
    private Integer partNumber;

    @Excel(name = "质量（Kg）")
    private String weight;

    @Excel(name = "图纸张数")
    @NotNull(message = "图纸张数不能为空")
    private Integer paperNumber;

    /**
     * 可能时 组件名称，也可能是 组件号+组件名称，组件号是两位数字
     */
    @Excel(name = "组件名称")
    @NotEmpty(message = "组件名称不能为空")
    private String componentName;

    @Excel(name = "机加工单号信息")
    @NotEmpty(message = "机加工单号信息不能为空")
    private String processOrderCode;

    @Excel(name = "打印日期", dateFormat = "yyyy/MM/dd")
    @NotNull(message = "打印日期不能为空")
    private Date printDate;

    @Excel(name = "设计")
    @NotEmpty(message = "设计不能为空")
    private String designer;

    @Excel(name = "购买原因")
    @NotEmpty(message = "购买原因不能为空")
    private String reason;

    @Excel(name = "项目代码")
    @NotEmpty(message = "项目代码不能为空")
    private String projectCode;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ComponentPartCode {

        private String componentCode;

        private String partCode;

        private String partVersion;
    }

    public ComponentPartCode validAndGetComponentPartCodeVersion() {
        String[] strs = this.paperCode.split("-");
        if (strs.length != 3) {
            throw new ServiceException(StrUtil.format("图号'{}'格式错误", this.paperCode));
        }
        String componentCode = strs[0] + "-" + strs[1];
        String partCode = null;
        String partVersion= null;
        if (strs[2].length() > 2){
            char secondLastChar = this.paperCode.charAt(this.paperCode.length() - 2);
            if (secondLastChar == 'V' || secondLastChar == 'v') {
                partCode = this.paperCode.substring(0, this.paperCode.length() - 2);
                partVersion = this.paperCode.substring(this.paperCode.length() - 2).toUpperCase();
            }
        }
        if (partCode == null) {
            partCode = this.paperCode;
        }
        if (partVersion == null) {
            partVersion = "V0";
        }
        return ComponentPartCode.builder().componentCode(componentCode).partCode(partCode).partVersion(partVersion).build();
    }
}
