package com.greenstone.mes.material.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.material.application.dto.result.IssueScanResult;
import com.greenstone.mes.material.infrastructure.persistence.IssueInvPo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialIssueInvMapper extends EasyBaseMapper<IssueInvPo> {

    List<IssueScanResult> selectScanData(String materialCode);

}
