package com.greenstone.mes.machine.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2024-09-10-10:25
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineRequirementAttachment {
    private String id;

    private String serialNo;

    private String name;

    private String path;
}
