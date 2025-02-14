package com.greenstone.mes.material.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialComplaintRecordAddReq {

    @NotNull(message = "material.complaint.record.taskId")
    private Long taskId;

    /**
     * 项目代码
     */
    @NotEmpty(message = "material.complaint.record.projectCode")
    private String projectCode;

    /**
     * 机加工单号
     */
    @NotEmpty(message = "material.complaint.record.partOrderCode")
    private String partOrderCode;

    /**
     * 组件号
     */
    @NotEmpty(message = "material.complaint.record.componentCode")
    private String componentCode;

    /**
     * 零件号
     */
    @NotEmpty(message = "material.complaint.record.orderCode")
    private String partCode;

    /**
     * 零件版本
     */
    @NotEmpty(message = "material.complaint.record.partVersion")
    private String partVersion;

    /**
     * 零件名称
     */
    private String partName;

    /**
     * 有问题数量
     */
    @NotNull(message = "material.complaint.record.number")
    @Min(value = 1, message = "common.attribute.validation.amount.mix")
    @Max(value = 999999, message = "common.attribute.validation.amount.max")
    private Long number;

    /**
     * 备注
     */
    private String remark;

    /**
     * 提问人id
     */
    @NotNull(message = "material.complaint.record.questioner")
    private Long questioner;

    /**
     * 提问人姓名
     */
    @NotEmpty(message = "material.complaint.record.questioner")
    private String questionerName;

    private List<MultipartFile> file;

    private List<String> fileBase64;
}
