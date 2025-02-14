package com.greenstone.mes.material.interfaces.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/12/14 9:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockTransferNgReq {
    private Integer operation;
    private Long inStockWhId;
    private Long outStockWhId;
    private Long partsGroupId;
    private String sponsor;
    private String remark;
    private List<MultipartFile> files;
    private Long materialId;
    private Long number;
    private String ngType;
    private String subNgType;
    private String componentCode;
    private String projectCode;
    private String worksheetCode;

}
