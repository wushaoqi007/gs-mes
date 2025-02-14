package com.greenstone.mes.material.event.data;

import com.greenstone.mes.material.domain.ProcessOrderDO;
import com.greenstone.mes.material.domain.ProcessOrderDetailDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-01-31-14:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConfirmEventData {

    private ProcessOrderDO processOrderDO;

    private List<ProcessOrderDetailDO> processOrderDetailDOList;
}
