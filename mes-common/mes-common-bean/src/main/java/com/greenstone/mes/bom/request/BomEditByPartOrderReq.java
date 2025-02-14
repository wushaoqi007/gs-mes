package com.greenstone.mes.bom.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * 通过修改机加工单导致的bom修改
 *
 * @author wushaoqi
 * @date 2022-09-20-13:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BomEditByPartOrderReq {

    /**
     * 组件code==bom的code
     */
    @NotEmpty(message = "组件号为空，无法查找bom")
    private String componentCode;

    @NotEmpty(message = "code为空，无法查找bom")
    private String materialCode;

    @NotEmpty(message = "code为空，无法查找bom")
    private String materialVersion;

    private Long materialNumber;

}
