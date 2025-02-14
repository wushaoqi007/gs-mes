package com.greenstone.mes.system.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-10-22-9:43
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MemberNavigationTreeResult {
    private List<Long> checkedNavigationIds;
    private List<NavigationTree> navigations;
}
