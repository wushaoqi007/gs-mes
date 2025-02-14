package com.greenstone.mes.material.interfaces.request;

import lombok.Data;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/12/14 9:53
 */
@Data
public class StockMobileTransferNgReq {
    private Integer operation;
    private Long inStockWhId;
    private Long outStockWhId;
    private String sponsor;
    private String remark;
    private List<Image> files;
    private Long materialId;
    private String ngType;
    private String subNgType;
    private Long number;
    private String componentCode;
    private String projectCode;
    private String worksheetCode;

    @Data
    public static class Image {
        private String imageBase64;
    }

}
