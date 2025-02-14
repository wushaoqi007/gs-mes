package com.greenstone.mes.ces.dto.cmd;

import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.system.api.domain.SysUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gu_renkai
 * @date 2023/3/6 14:33
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StateChangeCmd {

    private String serialNo;

    private ProcessStatus state;

    private SysUser approver;

    private String remark;

}
