package com.greenstone.mes.external.domain.repository;

import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.external.domain.converter.ProcessConverter;
import com.greenstone.mes.external.application.dto.cmd.CopyStatusChangeCmd;
import com.greenstone.mes.external.application.dto.result.ProcessCopyResult;
import com.greenstone.mes.external.domain.entity.ProcessCopy;
import com.greenstone.mes.external.infrastructure.enums.CopyHandleStatus;
import com.greenstone.mes.external.infrastructure.mapper.ProcessCopyMapper;
import com.greenstone.mes.external.infrastructure.persistence.ProcessCopyDO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Slf4j
@Service
public class ProcessCopyRepository {

    private final ProcessCopyMapper processCopyMapper;
    private final ProcessConverter converter;

    public List<ProcessCopyResult> copies() {
        return processCopyMapper.currUserCopies(SecurityUtils.getLoginUser().getUser().getUserId());
    }

    public void changeStatus(CopyStatusChangeCmd changeCmd) {
        for (String copyId : changeCmd.getCopyIds()) {
            ProcessCopyDO existCopy = processCopyMapper.selectById(copyId);
            if (existCopy == null) {
                throw new ServiceException("选择的抄送不存在");
            }
            ProcessCopyDO updateDO = ProcessCopyDO.builder().id(copyId).handleStatus(changeCmd.getHandleStatus()).build();
            switch (changeCmd.getHandleStatus()) {
                case NEW, UPDATED -> throw new ServiceException("不支持的操作");
                case HANDLED, IGNORED -> processCopyMapper.updateById(updateDO);
            }
        }
    }

    public void save(ProcessCopy processCopy) {
        ProcessCopyDO select = ProcessCopyDO.builder().serialNo(processCopy.getSerialNo())
                .userId(processCopy.getUserId()).build();
        ProcessCopyDO existCopy = processCopyMapper.getOneOnly(select);
        if (existCopy == null) {
            processCopyMapper.insert(converter.toProcessCopyDO(processCopy));
        } else if (existCopy.getHandleStatus() == CopyHandleStatus.HANDLED) {
            ProcessCopyDO updateCopy = ProcessCopyDO.builder().id(existCopy.getId()).handleStatus(CopyHandleStatus.UPDATED).build();
            processCopyMapper.updateById(updateCopy);
        }
    }

}
