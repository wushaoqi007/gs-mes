package com.greenstone.mes.bom.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * BOM导入详情响应
 *
 * @author wushaoqi
 * @date 2022-05-12-15:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BomImportDetailListResp {

    private Long importDetailId;

    private Long recordId;

    private String projectCode;

    private String componentName;

    private String codeVersion;
    private String code;
    private String version;

    private String name;

    private String buyLimit;

    private Long materialNumber;

    private Integer paperNumber;

    private String surfaceTreatment;

    private String rawMaterial;

    private String weight;

    private String designer;

    private Date printTime;

    private String remark;
}
