package com.greenstone.mes.bom.request;

import lombok.Getter;
import lombok.Setter;

/**
 * BOM导入记录请求
 *
 * @author wushaoqi
 * @date 2022-05-12-15:13
 */
@Getter
@Setter
public class BomImportRecordReq {

    private String projectCode;

    private String designer;

    private String startTime;

    private String endTime;
}
