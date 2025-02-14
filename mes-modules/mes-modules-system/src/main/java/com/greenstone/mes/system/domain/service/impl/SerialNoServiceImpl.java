package com.greenstone.mes.system.domain.service.impl;

import com.greenstone.mes.common.utils.NumberUtil;
import com.greenstone.mes.system.domain.SerialNo;
import com.greenstone.mes.system.domain.service.SerialNoService;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;
import com.greenstone.mes.system.infrastructure.mapper.SerialNoMapper;
import org.springframework.stereotype.Service;

/**
 * @author gu_renkai
 * @date 2023/2/6 11:07
 */
@Service
public class SerialNoServiceImpl implements SerialNoService {

    private final SerialNoMapper serialNoMapper;

    public SerialNoServiceImpl(SerialNoMapper serialNoMapper) {
        this.serialNoMapper = serialNoMapper;
    }


    @Override
    public synchronized SerialNoR getNext(SerialNoNextCmd nextCmd) {
        SerialNo snToFInd = SerialNo.builder().type(nextCmd.getType()).prefix(nextCmd.getPrefix()).build();
        SerialNo sn = serialNoMapper.getOneOnly(snToFInd);
        if (sn == null) {
            sn = new SerialNo();
            sn.setType(nextCmd.getType());
            sn.setPrefix(nextCmd.getPrefix());
            sn.setNumber(1L);
            serialNoMapper.insert(sn);
        } else {
            sn.setNumber(sn.getNumber() + 1);
            serialNoMapper.updateById(sn);
        }
        String snStr = sn.getPrefix() + NumberUtil.serialFormat(sn.getNumber());
        return SerialNoR.builder().serialNo(snStr).build();
    }

    @Override
    public synchronized SerialNoR getShortNext(SerialNoNextCmd nextCmd) {
        SerialNo snToFInd = SerialNo.builder().type(nextCmd.getType()).prefix(nextCmd.getPrefix()).build();
        SerialNo sn = serialNoMapper.getOneOnly(snToFInd);
        if (sn == null) {
            sn = new SerialNo();
            sn.setType(nextCmd.getType());
            sn.setPrefix(nextCmd.getPrefix());
            sn.setNumber(1L);
            serialNoMapper.insert(sn);
        } else {
            sn.setNumber(sn.getNumber() + 1);
            serialNoMapper.updateById(sn);
        }
        String snStr = sn.getPrefix() + NumberUtil.serialFormat2(sn.getNumber());
        return SerialNoR.builder().serialNo(snStr).build();
    }

    @Override
    public synchronized SerialNoR getNextContractNo(SerialNoNextCmd nextCmd) {
        SerialNo snToFInd = SerialNo.builder().type(nextCmd.getType()).prefix(nextCmd.getPrefix()).build();
        SerialNo sn = serialNoMapper.getOneOnly(snToFInd);
        if (sn == null) {
            sn = new SerialNo();
            sn.setType(nextCmd.getType());
            sn.setPrefix(nextCmd.getPrefix());
            sn.setNumber(1L);
            serialNoMapper.insert(sn);
        } else {
            sn.setNumber(sn.getNumber() + 1);
            serialNoMapper.updateById(sn);
        }
        String[] split = nextCmd.getType().split("_");
        String snStr = sn.getPrefix() + NumberUtil.contractFormat(sn.getNumber()) + split[1];
        return SerialNoR.builder().serialNo(snStr).build();
    }

}
