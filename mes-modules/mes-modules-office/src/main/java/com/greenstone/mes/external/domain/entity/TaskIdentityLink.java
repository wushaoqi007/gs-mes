package com.greenstone.mes.external.domain.entity;

import com.greenstone.mes.external.infrastructure.enums.ApproveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gu_renkai
 * @date 2023/3/7 15:33
 */

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TaskIdentityLink {

    private String id;
    private ApproveType type;
    private String taskId;
    private Long userId;
    private String userName;

}