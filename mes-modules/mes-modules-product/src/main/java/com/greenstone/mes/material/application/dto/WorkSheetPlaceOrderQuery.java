package com.greenstone.mes.material.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author wushaoqi
 * @date 2023-03-31-11:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkSheetPlaceOrderQuery {

    private String projectCode;

    private String designer;

    /**
     * 零件(物料)id
     */
    @NotNull(message = "零件不为空")
    private Long materialId;

}
