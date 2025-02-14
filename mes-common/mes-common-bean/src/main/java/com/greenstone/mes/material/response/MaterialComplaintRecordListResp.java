package com.greenstone.mes.material.response;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MaterialComplaintRecordListResp {

    private Long id;

    /**
     * 项目代码
     */
    private String projectCode;

    /**
     * 机加工单编号
     */
    private String orderCode;

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
     * NG数量
     */
    private Long ngNumber;

    /**
     * 备注
     */
    private String remark;

    /**
     * 质检员
     */
    private String inspector;

    /**
     * 加工商
     */
    private String provider;

    /**
     * 投诉时间
     */
    private Date createTime;
}
