package com.greenstone.mes.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.system.api.domain.SysRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;

    private String userName;

    private String nickName;

    private String employeeNo;

    private Long deptId;

    private Long roleId;

    private String wxUserId;

    private String wxCpId;

    private String email;

    private String phonenumber;

    private String sex;

    private String avatar;

    private String userType;

    private Dept dept;

    private String roleName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private SysRole role;

    public boolean isAdmin(){
        return "admin".equals(this.userType);
    }
}
