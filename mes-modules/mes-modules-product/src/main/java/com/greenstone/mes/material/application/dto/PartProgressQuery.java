package com.greenstone.mes.material.application.dto;

import lombok.Data;

/**
 * @author wushaoqi
 * @date 2023-06-26-15:21
 */
@Data
public class PartProgressQuery {

    private String projectCode;

    private String componentCode;

    private String partCode;
}
