package com.greenstone.mes.material.response;

import com.greenstone.mes.material.request.MaterialTaskAddReq;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MaterialTaskListResp {

    private Long id;

    /**
     * 项目代码
     */
    private String projectCode;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 负责人id
     */
    private Long leader;

    /**
     * 负责人姓名
     */
    private String leaderName;

    /**
     * 任务类型（1领料、2组装、3罩壳、4机构调试功能测试）
     */
    private Integer type;

    /**
     * 任务状态(0未开始、1进行中、2已完成、3已关闭)
     */
    private Integer status;

    /**
     * 任务进度
     */
    private Integer progress;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 成员列表
     */
    private List<MaterialTaskAddReq.MemberInfo> memberList;
}
