package com.greenstone.mes.system.dto.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserQuery {

    private List<Long> userIds;

    private List<Long> deptIds;

    private Long userId;

    private String userName;

    private String employeeNo;

    private Long deptId;

    private String nickName;

    private String wxCpId;

    private String wxUserId;

    private String email;

    private String phonenumber;

    private LocalDateTime beginCreateTime;

    private LocalDateTime endCreateTime;

}
