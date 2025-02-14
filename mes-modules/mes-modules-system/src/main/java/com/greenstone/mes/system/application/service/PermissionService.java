package com.greenstone.mes.system.application.service;

import com.greenstone.mes.system.application.dto.cmd.FunctionPermissionSaveCmd;
import com.greenstone.mes.system.application.dto.cmd.MemberPermissionSaveCmd;
import com.greenstone.mes.system.application.dto.result.FunctionPermissionWithMembersResult;
import com.greenstone.mes.system.application.dto.result.MemberNavigationTreeResult;

import java.util.List;
import java.util.Map;

/**
 * @author wushaoqi
 * @date 2024-10-22-9:40
 */
public interface PermissionService {

    MemberNavigationTreeResult selectMemberNavigationTree(Long memberId);

    Map<Long, Long> selectMemberPermissions(Long memberId);

    List<FunctionPermissionWithMembersResult> selectFunctionPermissionsWithMembers(Long functionId);

    void setMemberPermissions(MemberPermissionSaveCmd saveCmd);

    void setFunctionPermissions(FunctionPermissionSaveCmd saveCmd);

    void removePermission(Long memberId, Long functionPermissionId);

    void initFunctionPerm();
}
