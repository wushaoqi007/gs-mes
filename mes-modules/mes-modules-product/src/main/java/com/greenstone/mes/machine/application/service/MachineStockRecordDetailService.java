package com.greenstone.mes.machine.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.greenstone.mes.machine.infrastructure.persistence.MachineStockRecordDetail;
import com.greenstone.mes.material.response.MaterialInfoResp;

import java.util.List;

/**
 * 物料出入库记录明细Service接口
 *
 * @author gu_renkai
 * @date 2022-02-17
 */
public interface MachineStockRecordDetailService extends IService<MachineStockRecordDetail> {

    List<MaterialInfoResp> listStockRecordDetail(Long recordId);
}