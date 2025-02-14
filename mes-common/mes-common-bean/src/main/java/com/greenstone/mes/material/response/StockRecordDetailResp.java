package com.greenstone.mes.material.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class StockRecordDetailResp {

    private String sponsor;

    private String operator;

    private String warehouseName;

    private Integer operation;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime operationTime;

    private List<MaterialInfoResp> materialList;

}
