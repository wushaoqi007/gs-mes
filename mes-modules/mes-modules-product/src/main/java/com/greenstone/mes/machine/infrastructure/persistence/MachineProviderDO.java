package com.greenstone.mes.machine.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * @author wushaoqi
 * @date 2024-05-22-8:59
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "machine_provider")
public class MachineProviderDO extends BaseEntity {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String name;
    private String fullName;
    private String abbrName;
    private String email;
    private String contactName;
    private String contactPhone;
    private String phone;
    private String address;
    private String bank;
    private String account;
    private String taxNumber;
}
