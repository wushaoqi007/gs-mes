package com.greenstone.mes.oa.dto;

import com.greenstone.mes.common.core.annotation.Excel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class OaWxUserImportDto {

    @Excel(name = "姓名")
    private String name;

    @Excel(name = "帐号")
    private String userId;

    @Excel(name = "工号")
    private String employeeNo;

    @Excel(name = "职务")
    private String position;

    @Excel(name = "别名")
    private String alias;

    @Excel(name = "部门")
    private String dept;

    @Excel(name = "性别")
    private String sex;

    @Excel(name = "手机")
    private String phone;

    @Excel(name = "企业邮箱")
    private String email;

    @Excel(name = "激活状态")
    private String status;


}
