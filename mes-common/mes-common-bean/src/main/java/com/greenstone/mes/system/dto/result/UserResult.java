package com.greenstone.mes.system.dto.result;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResult {

    private Long userId;

    private Long deptId;

    private String deptName;

    private String userName;

    private String employeeNo;

    private String position;

    private String nickName;

    private String wxCpId;

    private String wxUserId;

    private String email;

    private String phonenumber;

    private String sex;

    private String avatar;

    private String password;

    private String status;

    private LocalDateTime createTime;

}
