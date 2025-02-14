package com.greenstone.mes.system.domain.entity;

import com.greenstone.mes.system.domain.Condition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-10-22-9:14
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MemberPermission {
    private Long id;
    private Long memberId;
    private String memberType;

    private Long functionPermissionId;
    private Long functionId;
    private String permissionGroupName;
    private String permissionGroupTypeName;
    private List<String> rights;
    private List<Condition> viewFilter;
    private List<Condition> updateFilter;

    private String functionName;
    private String functionType;
    private String source;
    private String component;
    private String formComponent;
    private Boolean usingProcess;
    private String templateId;
    private Integer orderNum;
}
