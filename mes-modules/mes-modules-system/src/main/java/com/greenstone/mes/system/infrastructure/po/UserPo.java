package com.greenstone.mes.system.infrastructure.po;

import com.baomidou.mybatisplus.annotation.*;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;
import java.time.LocalDateTime;

@ToString
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("sys_user")
public class UserPo extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long userId;

    private String userName;

    private String nickName;

    private String employeeNo;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long roleId;

    private Long deptId;

    private String position;

    private String wxUserId;

    @TableField("main_wxcp_id")
    private String wxCpId;

    private String email;

    private String phonenumber;

    private String sex;

    private String avatar;

    private String password;

    private String userType;

    @TableLogic
    private Boolean deleted;

    private String loginIp;

    private LocalDateTime loginDate;
}