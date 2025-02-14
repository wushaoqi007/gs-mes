package com.greenstone.mes.material.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MaterialTaskAddReq {


    /**
     * 项目
     */
    @NotEmpty(message = "项目代码不能为空")
    private String projectCode;

    /**
     * 任务名称
     */
    @NotEmpty(message = "任务名称不能为空")
    private String taskName;

    /**
     * 组件列表
     */
    private List<BomInfo> bomList;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class BomInfo {
        /**
         * bom的id
         */
        private Long bomId;

        /**
         * bom的名称
         */
        private String name;
    }

    /**
     * 负责人
     */
    private Long leader;

    /**
     * 任务类型（1领料、2组装、3罩壳、4机构调试功能测试）
     */
    private Integer type;

    /**
     * 成员列表
     */
    private List<MemberInfo> memberList;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class MemberInfo {
        /**
         * 成员id
         */
        private Long memberId;

        /**
         * 成员姓名
         */
        private String memberName;

        /**
         * 成员类型0:负责人；1：成员
         */
        private Integer memberType;
    }

    /**
     * 纳期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deadline;


}
