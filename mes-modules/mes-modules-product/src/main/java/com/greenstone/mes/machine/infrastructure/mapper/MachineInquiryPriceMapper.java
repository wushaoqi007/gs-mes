package com.greenstone.mes.machine.infrastructure.mapper;

import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachinePartScanQuery;
import com.greenstone.mes.machine.domain.entity.MachineInquiryPrice;
import com.greenstone.mes.machine.infrastructure.persistence.MachineInquiryPriceDO;
import com.greenstone.mes.table.infrastructure.config.mubatisplus.TableBaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MachineInquiryPriceMapper extends TableBaseMapper<MachineInquiryPriceDO> {

    List<MachineInquiryPrice> selectListByFuzzy(MachineFuzzyQuery fuzzyQuery);

    List<MachineInquiryPrice> selectListByScan(MachinePartScanQuery query);
}
