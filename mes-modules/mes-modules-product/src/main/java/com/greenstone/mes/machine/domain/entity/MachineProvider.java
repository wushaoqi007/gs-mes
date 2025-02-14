package com.greenstone.mes.machine.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2024-05-22-8:59
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MachineProvider {
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
