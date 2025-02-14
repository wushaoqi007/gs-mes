package com.greenstone.mes.machine.application.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.machine.application.service.MachineStockRecordDetailService;
import com.greenstone.mes.machine.infrastructure.mapper.MachineStockRecordDetailMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineStockRecordDetail;
import com.greenstone.mes.material.response.MaterialInfoResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 物料出入库记录明细Service业务层处理
 *
 * @author gu_renkai
 * @date 2022-02-17
 */
@Service
public class MachineStockRecordDetailServiceImpl extends ServiceImpl<MachineStockRecordDetailMapper, MachineStockRecordDetail> implements MachineStockRecordDetailService {

    @Autowired
    private MachineStockRecordDetailMapper machineStockRecordDetailMapper;

    @Override
    public List<MaterialInfoResp> listStockRecordDetail(Long recordId) {
        return machineStockRecordDetailMapper.listStockRecordDetail(recordId);
    }

}