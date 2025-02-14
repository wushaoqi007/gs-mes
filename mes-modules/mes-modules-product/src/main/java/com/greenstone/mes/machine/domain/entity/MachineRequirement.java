package com.greenstone.mes.machine.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.table.FlowFieldType;
import com.greenstone.mes.table.TableEntity;
import com.greenstone.mes.table.WorkflowField;
import com.greenstone.mes.table.infrastructure.annotation.StreamField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author wushaoqi
 * @date 2023-11-23-15:25
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MachineRequirement extends TableEntity {
    private Boolean checked;
    @StreamField("项目代码")
    private String projectCode;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime confirmTime;
    private Long confirmBy;
    private User confirmByUser;
    private String remark;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @StreamField("期望到货时间")
    @WorkflowField("expectDay")
    private LocalDateTime receiveDeadline;
    @StreamField("标题")
    @WorkflowField
    private String title;
    @StreamField("内容")
    private String content;
    @StreamField("审批人")
    @WorkflowField("approverId")
    private List<Long> approvers;
    @StreamField("抄送人")
    @WorkflowField("copyUserIds")
    private List<Long> copyTo;

    private Integer mailStatus;
    private String mailMsg;

    @StreamField("零件列表")
    @Builder.Default
    private List<MachineRequirementDetail> parts = new ArrayList<>();

    private MachineRequirementChangeReason changeReason;

    @WorkflowField(fieldType = FlowFieldType.DETAIL_LINK)
    private String detailLink;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MachineRequirement that = (MachineRequirement) o;

        if (!Objects.equals(checked, that.checked)) return false;
        if (!Objects.equals(projectCode, that.projectCode)) return false;
        if (!Objects.equals(confirmTime, that.confirmTime)) return false;
        if (!Objects.equals(confirmBy, that.confirmBy)) return false;
        if (!Objects.equals(confirmByUser, that.confirmByUser)) return false;
        if (!Objects.equals(remark, that.remark)) return false;
        if (!Objects.equals(receiveDeadline, that.receiveDeadline)) return false;
        if (!Objects.equals(title, that.title)) return false;
        if (!Objects.equals(content, that.content)) return false;
        if (!Objects.equals(approvers, that.approvers)) return false;
        if (!Objects.equals(copyTo, that.copyTo)) return false;
        if (!Objects.equals(mailStatus, that.mailStatus)) return false;
        if (!Objects.equals(mailMsg, that.mailMsg)) return false;
        if (!Objects.equals(parts, that.parts)) return false;
        if (!Objects.equals(changeReason, that.changeReason)) return false;
        return Objects.equals(detailLink, that.detailLink);
    }

    @Override
    public int hashCode() {
        int result = checked != null ? checked.hashCode() : 0;
        result = 31 * result + (projectCode != null ? projectCode.hashCode() : 0);
        result = 31 * result + (confirmTime != null ? confirmTime.hashCode() : 0);
        result = 31 * result + (confirmBy != null ? confirmBy.hashCode() : 0);
        result = 31 * result + (confirmByUser != null ? confirmByUser.hashCode() : 0);
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        result = 31 * result + (receiveDeadline != null ? receiveDeadline.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (approvers != null ? approvers.hashCode() : 0);
        result = 31 * result + (copyTo != null ? copyTo.hashCode() : 0);
        result = 31 * result + (mailStatus != null ? mailStatus.hashCode() : 0);
        result = 31 * result + (mailMsg != null ? mailMsg.hashCode() : 0);
        result = 31 * result + (parts != null ? parts.hashCode() : 0);
        result = 31 * result + (changeReason != null ? changeReason.hashCode() : 0);
        result = 31 * result + (detailLink != null ? detailLink.hashCode() : 0);
        return result;
    }
}
