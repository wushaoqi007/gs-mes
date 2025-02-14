package com.greenstone.mes.system.domain.entity;

import com.greenstone.mes.common.core.annotation.TreeChildren;
import com.greenstone.mes.common.core.annotation.TreeId;
import com.greenstone.mes.common.core.annotation.TreeParentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysDept2 {

    @TreeId
    private Long deptId;

    /**
     * 父部门ID
     */
    @TreeParentId
    private Long parentId;

    @TreeChildren
    private List<SysDept2> children;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 企业微信ID
     */
    private String cpId;

    /**
     * 微信部门id
     */
    private Long wxDeptId;

    /**
     * 显示顺序
     */
    private String orderNum;

    /**
     * 负责人
     */
    private String leader;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 部门状态:0正常,1停用
     */
    private String status;

    /**
     * 默认角色id
     */
    private String defaultRoleId;


}
