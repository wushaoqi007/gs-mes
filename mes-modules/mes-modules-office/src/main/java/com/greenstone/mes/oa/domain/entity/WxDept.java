package com.greenstone.mes.oa.domain.entity;

import com.greenstone.mes.wxcp.domain.types.WxDeptId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gu_renkai
 * @date 2022/11/24 13:22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WxDept {

    private WxDeptId deptId;

    private String name;

    private String fullName;

}
