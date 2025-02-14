package com.greenstone.mes.system.domain.service;

import com.greenstone.mes.system.application.dto.cmd.PermAddCmd;
import com.greenstone.mes.system.application.dto.cmd.PermImportReq;
import com.greenstone.mes.system.application.dto.cmd.PermMoveCmd;
import com.greenstone.mes.system.application.dto.cmd.RolePermEditCmd;
import com.greenstone.mes.system.application.dto.result.PermTree;
import com.greenstone.mes.system.infrastructure.po.PermPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PermService {

    List<PermTree> buildPermTree(List<PermPo> permPoList);

    PermPo addPerm(PermAddCmd addCmd);

    List<PermPo> list(PermPo perm);

    void movePerm(PermMoveCmd moveCmd);

    void delete(PermPo perm);

    void update(PermPo perm);

    List<String> selectPermListByRoleId(Long roleId);

    void updateRolePerm(RolePermEditCmd editCmd);

    void importPerms(List<PermImportReq> permImportReqs);

    List<String> selectRolePermsByUserId(@Param("userId") Long userId);

}
