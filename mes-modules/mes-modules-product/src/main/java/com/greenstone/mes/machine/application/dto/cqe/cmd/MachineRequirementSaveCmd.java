package com.greenstone.mes.machine.application.dto.cqe.cmd;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.common.core.enums.MachineError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.machine.infrastructure.enums.RequirementPartType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineRequirementSaveCmd {
    private String id;
    private String serialNo;
    private String projectCode;
    private Long applyById;
    private String applyBy;
    private String applyByNo;
    private LocalDateTime applyTime;
    @NotNull(message = "请选择期望到货日期")
    private LocalDateTime receiveDeadline;

    @NotEmpty(message = "请填写标题")
    private String title;

    @NotEmpty(message = "请填写内容")
    private String content;

    @NotEmpty(message = "请选择审批人")
    private List<Long> approvers;

    @Size(max = 9, message = "抄送人不超过9人")
    private List<Long> copyTo;

//    @NotNull(message = "请添加附件")
//    private MultipartFile file;

    @Valid
    @NotEmpty(message = "请先上传附件")
    @Size(max = 1, message = "最多上传一个附件")
    private List<Attachment> attachments;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Attachment {
        @NotEmpty(message = "请填写附件名称")
        private String name;
        @NotEmpty(message = "请填写附件路径")
        private String path;
    }

    private List<Part> parts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        @NotEmpty(message = "需求单号不为空")
        private String serialNo;
        @NotEmpty(message = "项目代码不为空")
        private String projectCode;
        @NotEmpty(message = "零件号不为空")
        private String partCode;
        @NotEmpty(message = "零件名称不为空")
        private String partName;
        @NotEmpty(message = "零件版本不为空")
        private String partVersion;
        @NotNull(message = "单套数量不为空")
        private Long perSet;
        @NotNull(message = "套数不为空")
        private Integer setsNumber;
        private Long originalNumber;
        private Long processNumber;
        @NotNull(message = "图纸数量不为空")
        private Integer paperNumber;
        private String surfaceTreatment;
        private String rawMaterial;
        private String weight;
        @JsonFormat(pattern = "yyyy/MM/dd")
        @NotNull(message = "打印日期不为空")
        private Date printDate;
        private String hierarchy;
        @NotEmpty(message = "设计不为空")
        private String designer;
        private String remark;
        private String description;
        private RequirementPartType type;
        private String oldSerialNo;
        private String oldPartVersion;

        public void trim() {
            List<String> trims = List.of(" ", "-", "_", "/");
            partName = StrUtil.trim(partName, 0, character -> trims.contains(String.valueOf(character)));
            partName = partName.replaceAll("\r", "");
            partName = partName.replaceAll("\n", "");
            if (StrUtil.isNotEmpty(rawMaterial)) {
                rawMaterial = StrUtil.trim(rawMaterial, 0, character -> trims.contains(String.valueOf(character)));
                rawMaterial = rawMaterial.replaceAll("\r", "");
                rawMaterial = rawMaterial.replaceAll("\n", "");
            }
            if (StrUtil.isNotEmpty(surfaceTreatment)) {
                surfaceTreatment = StrUtil.trim(surfaceTreatment, 0, character -> trims.contains(String.valueOf(character)));
                surfaceTreatment = surfaceTreatment.replaceAll("\r", "");
                surfaceTreatment = surfaceTreatment.replaceAll("\n", "");
            }
            if (StrUtil.isNotEmpty(hierarchy)) {
                hierarchy = StrUtil.trim(hierarchy, 0, character -> trims.contains(String.valueOf(character)));
                hierarchy = hierarchy.replaceAll("\r", "");
                hierarchy = hierarchy.replaceAll("\n", "");
            }
        }
    }


    public void trim() {
        for (Part part : this.parts) {
            part.trim();
        }
    }

    public void validate() {
        this.validateMachineRequirement();
    }

    private void validateMachineRequirement() {
        String serialNo = null;
        String projectCode = null;
        for (Part part : this.parts) {
            // 同一次上传的加工单号必须一致
            if (serialNo == null) {
                serialNo = part.getSerialNo();
            } else if (!serialNo.equals(part.getSerialNo())) {
                throw new ServiceException(MachineError.E200003, StrUtil.format("'{}'和'{}'", serialNo, part.getSerialNo()));
            }
            // 同一次上传的项目号必须一致
            if (projectCode == null) {
                projectCode = part.getProjectCode();
            } else if (!projectCode.equals(part.getProjectCode())) {
                throw new ServiceException(MachineError.E200004, StrUtil.format("'{}'和'{}'", projectCode, part.getProjectCode()));
            }
            // 零件号格式为4个字母+4个数字
            char[] chars = part.getPartCode().toCharArray();
            int count1 = 0;
            int count2 = 0;
            for (char c : chars) {
                if (Character.isLetter(c)) {
                    count1++;
                } else if (Character.isDigit(c)) {
                    count2++;
                }
            }
            if (count1 != 4 || count2 != 4) {
                throw new ServiceException(StrUtil.format("零件号应由4个字母+4个数字组成：{}", part.getPartCode()));
            }
            // 零件类型校验
            if (part.getType() == RequirementPartType.NEW && (StrUtil.isNotBlank(part.getOldSerialNo()) || StrUtil.isNotBlank(part.getOldPartVersion()))) {
                throw new ServiceException(StrUtil.format("新制零件，无需原申请单号和原版本，零件号：{}", part.getPartCode()));
            }
            if (part.getType() == RequirementPartType.CANCEL) {
                if (StrUtil.isBlank(part.getOldSerialNo())) {
                    throw new ServiceException(StrUtil.format("取消零件，原申请单号不为空，零件号：{}", part.getPartCode()));
                }
                if (part.getOldSerialNo().equals(part.getSerialNo())) {
                    throw new ServiceException(StrUtil.format("取消零件，原申请单号和现申请单号不能一样，申请单号：{}，零件号/版本：{}/{}", part.getOldSerialNo(), part.getPartCode(), part.getPartVersion()));
                }
            }
            if (part.getType() == RequirementPartType.UPDATE) {
                if (StrUtil.isBlank(part.getOldSerialNo()) || StrUtil.isBlank(part.getOldPartVersion())) {
                    throw new ServiceException(StrUtil.format("修改零件，原申请单号和原版本不为空，零件号：{}", part.getPartCode()));
                }
                if (part.getOldSerialNo().equals(part.getSerialNo())) {
                    throw new ServiceException(StrUtil.format("修改零件，原申请单号和现申请单号不能一样，申请单号：{}，零件号/版本：{}/{}", part.getOldSerialNo(), part.getPartCode(), part.getPartVersion()));
                }
            }
        }
        // 加工单号的规则：客户代码+7位数字+8位日期+3位字母+2位序号
        if (!serialNo.matches("[A-Z]+[0-9]{15}[A-Z]{3}[0-9]+") && !serialNo.matches("[A-Z]+[0-9]{4}[A-Z]{2}[0-9]{3}[0-9]{8}[A-Z]{3}[0-9]+")) {
            throw new ServiceException(MachineError.E200002, StrUtil.format("客户代码+（7位数字）或（4位数字+2位字母+3位数字）+8位日期+3位字母+2位序号。", serialNo));
        }
    }
}
