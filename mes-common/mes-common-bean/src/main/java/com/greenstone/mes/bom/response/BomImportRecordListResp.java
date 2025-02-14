package com.greenstone.mes.bom.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * BOM导入记录响应
 *
 * @author wushaoqi
 * @date 2022-05-12-15:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BomImportRecordListResp {

    private Long importRecordId;

    private String fileName;

    private String projectCode;

    private Integer rowCount;

    private String designer;

    private Date updateTime;

}
