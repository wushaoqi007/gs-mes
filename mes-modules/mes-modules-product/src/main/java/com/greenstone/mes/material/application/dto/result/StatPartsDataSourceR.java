package com.greenstone.mes.material.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 日统计零件数据来源
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatPartsDataSourceR {

    /**
     * 加工单位
     */
    private String provider;

    /**
     * 发图日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date uploadTime;

    /**
     * 生产代码
     */
    private String projectCode;

    /**
     * 机种名称
     */
    private String componentName;

    /**
     * 零件名称
     */
    private String partName;

    /**
     * 零件号
     */
    private String partCode;

    /**
     * 零件版本
     */
    private String partVersion;

    /**
     * 材料
     */
    private String rawMaterial;

    /**
     * 订单数量
     */
    private Integer partNum;

    private Integer paperNumber;

    /**
     * 交货数量
     */
    private Integer deliveryNum;

    /**
     * 交货日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deliveryTime;

    /**
     * 加工纳期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date processingTime;

    /**
     * 计划纳期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date planTime;

    /**
     * 设计
     */
    private String designer;

}
