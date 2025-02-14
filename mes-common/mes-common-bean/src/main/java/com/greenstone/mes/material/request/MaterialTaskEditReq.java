package com.greenstone.mes.material.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MaterialTaskEditReq {

    @NotNull(message = "ID不为空")
    private Long id;

    /**
     * 项目
     */
    private String projectCode;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 组件列表
     */
    private List<MaterialTaskAddReq.BomInfo> bomList;

    /**
     * 负责人
     */
    private Long leader;

    /**
     * 任务类型（1领料、2组装、3罩壳、4机构调试功能测试）
     */
    private Integer type;

    /**
     * 任务状态(0未开始、1进行中、2已完成、3已关闭)
     */
    private Integer status;

    /**
     * 成员列表
     */
    private List<MaterialTaskAddReq.MemberInfo> memberList;


    /**
     * 纳期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deadline;

}
