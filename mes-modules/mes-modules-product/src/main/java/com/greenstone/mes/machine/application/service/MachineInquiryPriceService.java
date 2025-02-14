package com.greenstone.mes.machine.application.service;

import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineInquiryPriceSendCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachinePartScanQuery;
import com.greenstone.mes.machine.application.dto.result.MachineInquiryPriceResult;
import com.greenstone.mes.machine.application.dto.result.MachineRequirementDetailResult;
import com.greenstone.mes.machine.domain.entity.MachineInquiryPrice;
import com.greenstone.mes.machine.domain.entity.MachineRequirementDetail;
import com.greenstone.mes.machine.infrastructure.mapper.MachineInquiryPriceMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineInquiryPriceDO;
import com.greenstone.mes.table.core.TableService;

import java.util.List;

public interface MachineInquiryPriceService extends TableService<MachineInquiryPrice, MachineInquiryPriceDO, MachineInquiryPriceMapper> {

    MachineRequirementDetail scan(MachinePartScanQuery query);

    void sendInquiryPrice(MachineInquiryPriceSendCmd sendCmd);

    List<MachineInquiryPriceResult> searchByScan(MachinePartScanQuery query);
}
