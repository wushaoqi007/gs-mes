package com.greenstone.mes.material.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class WorksheetDetailListReq {

    /**
     * 加工单号
     */
    private String worksheetCode;

    /**
     * 设计
     */
    private String designer;

    /**
     * 项目代码
     */
    private String projectCode;

    /**
     * 指零件号或零件名称
     */
    private String part;

}
