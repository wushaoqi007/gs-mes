package com.greenstone.mes.oa.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * @author gu_renkai
 * @Date 2022/8/2 16:54
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("oa_wx_cp_sys_user")
public class WxCpSysUser extends BaseEntity {

    @TableField
    private Long userId;

    @TableField
    private String wxUserId;

    @TableField
    private String wxCpId;

}
