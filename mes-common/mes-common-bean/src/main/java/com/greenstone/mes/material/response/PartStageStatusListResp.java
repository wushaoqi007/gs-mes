
package com.greenstone.mes.material.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PartStageStatusListResp {


    private Integer stage;

    private Integer stockNum;
    private Integer inStockTotal;
    private Integer outStockTotal;

    private Long materialId;

    private String projectCode;

    private String componentCode;

    private String componentName;

    private String partCode;

    private String partVersion;

    private String partName;

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
    private String createBy;

    /**
     * 完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date finishTime;
    private Date firstInTime;
    private Date firstOutTime;


    private List<WorksheetProgress> progressList;

    private List<Stock> stockList;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class WorksheetProgress {

        /**
         * 进度（例如：待收件、已完成等）
         */
        private String progress;

        private Integer number;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class Stock {

        /**
         * 仓库
         */
        private String warehouseName;

        private Integer number;
    }
}
