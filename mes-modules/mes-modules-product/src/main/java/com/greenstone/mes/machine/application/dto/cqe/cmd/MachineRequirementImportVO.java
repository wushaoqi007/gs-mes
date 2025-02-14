package com.greenstone.mes.machine.application.dto.cqe.cmd;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.greenstone.mes.common.core.annotation.Excel;
import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

/**
 * 机加工单导入
 */
@Data
public class MachineRequirementImportVO {

    @Excel(name = "序号")
    @NotEmpty(message = "序号不能为空")
    private String seqNum;

    @Excel(name = "申请单号")
    @NotEmpty(message = "申请单号不能为空")
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

    @Excel(name = "单套数量")
    @NotNull(message = "单套数量不能为空:请使用新版的打印软件生成表格")
    private Integer partNumber;

    @Excel(name = "套数")
    @NotNull(message = "套数不能为空:请使用新版的打印软件生成表格")
    private Integer setsNumber;

    @Excel(name = "零件总数")
    private Integer totalNumber;

    @Excel(name = "图纸张数")
    @NotNull(message = "图纸张数不能为空")
    private Integer paperNumber;

    @Excel(name = "表面处理")
    private String surfaceTreatment;

    @Excel(name = "质量（g）")
    private String weight;

    @Excel(name = "层级结构")
    @NotEmpty(message = "层级结构不能为空:请使用新版的打印软件生成表格")
    private String hierarchy;

    @Excel(name = "打印日期", dateFormat = "yyyy/MM/dd")
    @NotNull(message = "打印日期不能为空")
    private Date printDate;

    @Excel(name = "设计")
    @NotEmpty(message = "设计不能为空")
    private String designer;

    @Excel(name = "备注")
    private String remark;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
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
                version = nameVersion.substring(nameVersion.length() - 2);
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

    public Long calProcessNumber() {
        if (Objects.nonNull(this.totalNumber)) {
            return totalNumber.longValue();
        } else {
            return this.partNumber * setsNumber.longValue();
        }
    }

}
