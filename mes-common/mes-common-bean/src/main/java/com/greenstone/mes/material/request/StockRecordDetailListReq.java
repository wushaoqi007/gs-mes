package com.greenstone.mes.material.request;

import lombok.*;

import java.util.Date;

/**
 * 查询出入库记录明细列表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class StockRecordDetailListReq {

    private Long materialId;

}
