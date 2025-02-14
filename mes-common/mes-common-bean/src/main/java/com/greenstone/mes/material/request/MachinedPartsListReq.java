package com.greenstone.mes.material.request;

import lombok.Data;

import java.util.Date;

@Data
public class MachinedPartsListReq {

    private String projectCode;

    private String componentCode;

    private String componentName;

    private String componentVersion;

    private String materialCode;

    private String materialName;

    private String materialVersion;

    private String designer;

    private String provider;

    private Date deliveryTimeFrom;

    private Date deliveryTimeTo;

}
