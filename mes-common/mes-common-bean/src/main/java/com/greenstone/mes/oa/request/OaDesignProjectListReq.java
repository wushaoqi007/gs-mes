package com.greenstone.mes.oa.request;

import lombok.*;

/**
 * 设计部门项目对象 oa_design_project
 *
 * @author gu_renkai
 * @date 2022-05-24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OaDesignProjectListReq {

    /**
     * 业务代码
     */
    private String projectCode;

    /**
     * 业务名称
     */
    private String projectName;

    /**
     * 组长
     */
    private String groupLeader;

    /**
     * 设计担当
     */
    private String designer;

    /**
     * 类型
     */
    private Integer projectType;
}