package com.greenstone.mes.wxcp.domain.types;

import lombok.Data;

@Data
public class WxUser {

    private String userId;

    private String newUserId;

    private String name;

    private String cpId;

    private String employeeNo;

    private Long[] departIds;

    private String position;

    private String mainDepartment;

    private String openUserId;

}
