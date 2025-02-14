package com.greenstone.mes.product.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductPlanQuery {

    private Long parentId;
    private Integer planType;

    private List<String> queryStatus;

    private String projectCode;

    private String startTime;
    private String endTime;

}
