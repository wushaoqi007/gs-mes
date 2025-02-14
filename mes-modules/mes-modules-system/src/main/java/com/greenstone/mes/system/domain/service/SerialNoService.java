package com.greenstone.mes.system.domain.service;


import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;

/**
 * @author gu_renkai
 * @date 2023/2/6 11:06
 */

public interface SerialNoService {

    SerialNoR getNext(SerialNoNextCmd nextCmd);

    SerialNoR getShortNext(SerialNoNextCmd nextCmd);

    SerialNoR getNextContractNo(SerialNoNextCmd nextCmd);

}
