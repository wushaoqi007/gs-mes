package com.greenstone.mes.system.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2024-10-28-8:25
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Condition {

    private String field;

    private String cnd;

    private String value;
}
