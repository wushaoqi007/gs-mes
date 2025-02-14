package com.greenstone.mes.material.request;

import lombok.Data;

@Data
public class MaterialReceivingListReq {

    /**
     * 项目
     */
    private String projectCode;

    /**
     * 状态(0待接收、1备料中、2待领料、3已完成、4已关闭)
     */
    private Integer status;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 接收人
     */
    private String receiveBy;
}
