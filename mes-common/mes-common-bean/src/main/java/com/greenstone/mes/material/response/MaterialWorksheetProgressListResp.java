
package com.greenstone.mes.material.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;
import java.util.List;

/**
 * 进度完整信息
 *
 * @author wushaoqi
 * @date 2022-11-09-13:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MaterialWorksheetProgressListResp {

    private Integer stage;

    private String projectCode;

    private String componentCode;

    private String componentName;

    private String partCode;

    private String partVersion;

    private String partName;

    private Long materialId;

    private int total;

    private String surfaceTreatment;

    private String rawMaterial;

    private String weight;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date printDate;

    /**
     * 加工单位
     */
    private String provider;

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
     * 收货日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date receivingTime;

    private String designer;

    /**
     * 完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date finishTime;


    private List<PartStageStatusListResp.WorksheetProgress> progressList;

    private List<PartStageStatusListResp.Stock> stockList;


}
