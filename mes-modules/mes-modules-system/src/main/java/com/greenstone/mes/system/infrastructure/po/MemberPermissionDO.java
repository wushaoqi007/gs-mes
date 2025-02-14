package com.greenstone.mes.system.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2024-10-22-9:14
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "sys_member_permission")
public class MemberPermissionDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long memberId;
    private String memberType;
    private Long functionPermissionId;
}
