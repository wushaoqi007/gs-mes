package com.greenstone.mes.oa.application.dto.wx;

import com.greenstone.mes.common.core.annotation.Excel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class OaWxUserLeaderImportDto {

    @Excel(name = "姓名")
    private String name;

    @Excel(name = "工号")
    private String employeeNo;

    @Excel(name = "上级姓名")
    private String leaderName;

    @Excel(name = "上级工号")
    private String leaderEmployeeNo;


}
