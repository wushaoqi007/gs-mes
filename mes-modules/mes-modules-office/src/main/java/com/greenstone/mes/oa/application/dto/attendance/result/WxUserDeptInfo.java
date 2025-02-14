package com.greenstone.mes.oa.application.dto.attendance.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WxUserDeptInfo {

    private String userId;

    private String nickname;

    private String deptName;

}
