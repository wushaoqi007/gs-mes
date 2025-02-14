package com.greenstone.mes.material.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 在制项目零件统计数据来源
 *
 * @author wushaoqi
 * @date 2023-02-22-14:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatDataParts {


    private Long id;

    private Long worksheetDetailId;

    private Integer stage;

    private String customerName;

    private String customerShortName;

    private Integer stockNum;

    private Integer inStockTotal;

    private Integer outStockTotal;

    private String projectCode;
    private String componentCode;
    private String componentName;
    private Date uploadTime;
    private Date planTime;
    private Integer paperNum;
    private String remark;
}
