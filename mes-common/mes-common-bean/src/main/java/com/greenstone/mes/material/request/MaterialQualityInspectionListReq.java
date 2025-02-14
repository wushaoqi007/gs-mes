package com.greenstone.mes.material.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2022-10-24-10:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialQualityInspectionListReq {

    /**
     * 项目代码
     */
    private String projectCode;

    /**
     * 机加工单编号
     */
    private String partOrderCode;

    /**
     * 零件号
     */
    private String code;

    /**
     * 版本
     */
    private String version;

    /**
     * 零件名称
     */
    private String name;

    /**
     * 质检结果
     */
    private String result;


    /**
     * NG大类
     */
    private String ngType;

    /**
     * NG小类
     */
    private String ngSubclass;

    /**
     * 检验人
     */
    private String createBy;
}
