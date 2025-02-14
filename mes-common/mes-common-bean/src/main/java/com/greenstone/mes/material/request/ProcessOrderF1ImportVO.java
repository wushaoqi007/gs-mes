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
 * 机加工单导入
 */
@Data
@Valid
public class ProcessOrderF1ImportVO {

    @Excel(name = "序号")
    @NotEmpty(message = "序号不能为空")
    private String seqNum;

    @Excel(name = "机加工单号信息")
    @NotEmpty(message = "机加工单号信息不能为空")
    private String processOrderCode;

    @Excel(name = "项目代码")
    @NotEmpty(message = "项目代码不能为空")
    private String projectCode;

    /**
     * code+空格+name+version，版本可以为空
     */
    @Excel(name = "零件号")
    @NotEmpty(message = "零件号不能为空")
    private String partCodeNameVersion;

    @Excel(name = "材料")
    private String rawMaterial;

    @Excel(name = "数量")
    @NotNull(message = "数量不能为空")
    private Integer partNumber;

    @Excel(name = "说明")
    private String remark;

    @Excel(name = "质量（g）")
    private String weight;

    /**
     * 可能时 组件名称，也可能是 组件号+组件名称，组件号是两位数字
     */
    @Excel(name = "组件名称")
    @NotEmpty(message = "组件不能为空")
    private String componentCodeName;

    @Excel(name = "打印日期", dateFormat = "yyyy/MM/dd")
    @NotNull(message = "打印日期不能为空")
    private Date printDate;

    @Excel(name = "设计")
    @NotEmpty(message = "设计不能为空")
    private String designer;

    @Excel(name = "表面处理")
    private String surfaceTreatment;

    @Excel(name = "图纸张数")
    @NotNull(message = "图纸张数不能为空")
    private Integer paperNumber;

    @Excel(name = "购买原因")
    @NotEmpty(message = "购买原因不能为空")
    private String reason;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComponentCodeName {
        private String code;

        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartCodeNameVersion {
        private String code;

        private String name;

        private String version;
    }

    public ComponentCodeName validAndGetComponentCodeName() {
        String componentCode;
        String componentName;
        if (this.componentCodeName.length() > 2 && StrUtil.isNumeric(this.componentCodeName.substring(0, 2))) {
            componentCode = this.projectCode + "-" + this.componentCodeName.substring(0, 2);
            componentName = componentCodeName.substring(2);
        } else {
            throw new ServiceException(StrUtil.format("组件名称'{}'错误，缺少编码，必须以两位数字开头", componentCodeName));
        }
        return ComponentCodeName.builder().code(componentCode).name(componentName).build();
    }

    public PartCodeNameVersion validAndGetPartCodeNameVersion() {
        String code;
        String name = null;
        String version = null;
        // codeNameVersion 如：IAYC7193 固定结构件V0
        // 拆分零件编码、名称、版本，编码、名称版本中间使用空格分割，如果有版本则名称和版本连在一起
        String[] codeNameVersion = partCodeNameVersion.split(" ");
        if (codeNameVersion.length < 2) {
            throw new ServiceException(StrUtil.format("零件名称不正确: {}", partCodeNameVersion));
        }
        code = codeNameVersion[0];
        // codeNameVersion 的倒数第二个字符为V或v时，表示最后两位是版本
        String nameVersion = partCodeNameVersion.substring(code.length() + 1);
        if (StrUtil.isEmpty(nameVersion)) {
            throw new ServiceException(StrUtil.format("零件名称版本不正确：{}", nameVersion));
        }
        if (nameVersion.length() > 2) {
            char secondLastChar = nameVersion.charAt(nameVersion.length() - 2);
            if (secondLastChar == 'V' || secondLastChar == 'v') {
                name = nameVersion.substring(0, nameVersion.length() - 2);
                version = nameVersion.substring(nameVersion.length() - 2).toUpperCase();
            }
        }
        if (name == null) {
            name = nameVersion;
        }
        if (version == null) {
            version = "V0";
        }
        return PartCodeNameVersion.builder().code(code).version(version).name(name).build();
    }

}
