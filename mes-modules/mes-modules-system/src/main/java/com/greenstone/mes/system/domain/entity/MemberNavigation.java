package com.greenstone.mes.system.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2024-10-17-16:25
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MemberNavigation {

    private Long id;

    private Long memberId;

    private String memberType;

    private Long navigationId;

    private String navigationName;

    private String category;

    private String navigationType;

    private Boolean active;

    private Long functionId;

}
