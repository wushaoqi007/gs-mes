package com.greenstone.mes.ces.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.ces.application.assembler.CesClearAssembler;
import com.greenstone.mes.ces.application.dto.cmd.CesClearAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.CesClearEditCmd;
import com.greenstone.mes.ces.application.dto.cmd.CesClearRemoveCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseOutAddCmd;
import com.greenstone.mes.ces.application.dto.event.CesClearAddE;
import com.greenstone.mes.ces.application.dto.query.CesClearFuzzyQuery;
import com.greenstone.mes.ces.application.dto.result.CesClearResult;
import com.greenstone.mes.ces.application.event.CesClearAddEvent;
import com.greenstone.mes.ces.application.service.CesClearService;
import com.greenstone.mes.ces.domain.entity.CesClear;
import com.greenstone.mes.ces.domain.entity.CesClearItem;
import com.greenstone.mes.ces.domain.entity.WarehouseStock;
import com.greenstone.mes.ces.domain.repository.CesClearRepository;
import com.greenstone.mes.ces.domain.repository.WarehouseStockRepository;
import com.greenstone.mes.ces.dto.cmd.CesClearStatusChangeCmd;
import com.greenstone.mes.common.core.enums.CesClearError;
import com.greenstone.mes.common.core.enums.WarehouseIOError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class CesClearServiceImpl implements CesClearService {

    private final CesClearRepository cesClearRepository;
    private final CesClearAssembler assembler;
    private final RemoteSystemService systemService;
    private final ApplicationEventPublisher eventPublisher;
    private final WarehouseStockRepository stockRepository;

    @Override
    public List<CesClearResult> list(CesClearFuzzyQuery query) {
        log.info("CesClearFuzzyQuery params:{}", query);
        List<CesClear> cesClears = cesClearRepository.list(query);
        return assembler.toCesClearRs(cesClears);
    }

    @Override
    public CesClearResult detail(String serialNo) {
        log.info("detail params:{}", serialNo);
        CesClear cesClear = cesClearRepository.detail(serialNo);
        return assembler.toCesClearR(cesClear);
    }

    @Override
    public void add(CesClearAddCmd addCmd) {
        log.info("CesClearAddCmd params:{}", addCmd);
        CesClear cesClear = assembler.toCesClear(addCmd);
        check(cesClear.getItems());

        SerialNoNextCmd nextCmd =
                SerialNoNextCmd.builder().type("ces_clear").prefix("CLR" + DateUtil.dateSerialStrNow()).build();
        SerialNoR serialNoR = systemService.getNextSn(nextCmd);

        cesClear.setSerialNo(serialNoR.getSerialNo());
        cesClear.setStatus(addCmd.isCommit() ? ProcessStatus.APPROVING : ProcessStatus.DRAFT);
        cesClear.setClearById(SecurityUtils.getLoginUser().getUser().getUserId());
        cesClear.setClearByName(SecurityUtils.getLoginUser().getUser().getNickName());
        cesClear.setClearByNo(SecurityUtils.getLoginUser().getUser().getEmployeeNo());
        log.info("add CesClear params:{}", cesClear);
        cesClearRepository.add(cesClear);

    }

    public void check(List<CesClearItem> items) {
        for (CesClearItem item : items) {
            // 校验：库存检查，不能清理库存不足的物品
            WarehouseStock stock = stockRepository.getByWarehouseAndItem(item.getWarehouseCode(), item.getItemCode());
            if (Objects.isNull(stock) || stock.getNumber() < item.getClearNum()) {
                throw new ServiceException(WarehouseIOError.E120105);
            }
        }
    }

    @Transactional
    @Override
    public void edit(CesClearEditCmd editCmd) {
        log.info("CesClearEditCmd params:{}", editCmd);
        CesClear clearFound = cesClearRepository.get(editCmd.getSerialNo());
        if (clearFound == null) {
            throw new ServiceException(CesClearError.E160101);
        }
        if (clearFound.getStatus() != ProcessStatus.DRAFT) {
            throw new ServiceException(CesClearError.E160102);
        }

//        if (editCmd.isCommit()) {
//            ProcessStartCmd startCmd = ProcessStartCmd.builder().formId("ces_cesClear").serialNo(editCmd.getSerialNo()).build();
//            flowService.createAndRun(startCmd);
//        }
        CesClear clear = assembler.toCesClear(editCmd);
        check(clear.getItems());

        clear.setStatus(editCmd.isCommit() ? ProcessStatus.COMMITTED : ProcessStatus.DRAFT);
        log.info("edit CesClear params:{}", clear);
        cesClearRepository.edit(clear);
    }

    @Override
    public void statusChange(CesClearStatusChangeCmd statusChangeCmd) {
        log.info("CesClearStatusChangeCmd params:{}", statusChangeCmd);
        if (statusChangeCmd.getStatus() == ProcessStatus.COMMITTED) {
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                CesClear cesClearFound = cesClearRepository.get(serialNo);
                if (cesClearFound == null) {
                    throw new ServiceException(CesClearError.E160101);
                }
                if (cesClearFound.getStatus() != ProcessStatus.DRAFT) {
                    throw new ServiceException(CesClearError.E160102);
                }
                cesClearRepository.changeStatus(CesClear.builder().status(statusChangeCmd.getStatus()).serialNo(serialNo).build());
//                ProcessStartCmd startCmd = ProcessStartCmd.builder().formId("ces_cesClear").serialNo(serialNo).build();
//                flowService.createAndRun(startCmd);
            }
        } else if (statusChangeCmd.getStatus() == ProcessStatus.CLOSED) {
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                CesClear cesClearFound = cesClearRepository.get(serialNo);
                if (cesClearFound == null) {
                    throw new ServiceException(CesClearError.E160101);
                }
                if (cesClearFound.getStatus() != ProcessStatus.APPROVED) {
                    throw new ServiceException(CesClearError.E160103);
                }
            }
            cesClearRepository.statusChange(statusChangeCmd);
        } else if (statusChangeCmd.getStatus() == ProcessStatus.APPROVED) {
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                CesClear cesClearFound = cesClearRepository.detail(serialNo);
                if (cesClearFound == null) {
                    throw new ServiceException(CesClearError.E160101);
                }
                cesClearRepository.changeStatus(CesClear.builder().status(statusChangeCmd.getStatus()).serialNo(serialNo).build());
                // 防止多次点击
                if (cesClearFound.getStatus() != ProcessStatus.APPROVED) {
                    eventPublisher.publishEvent(new CesClearAddEvent(CesClearAddE.builder().serialNo(cesClearFound.getSerialNo()).build()));
                }
            }
        } else {
            cesClearRepository.statusChange(statusChangeCmd);
        }
    }


    @Override
    public List<WarehouseOutAddCmd> createWarehouseOut(String serialNo) {
        List<WarehouseOutAddCmd> cmdList = new ArrayList<>();
        CesClear cesClear = cesClearRepository.detail(serialNo);
        if (Objects.nonNull(cesClear)) {
            // 清理单物品按照收货仓库分类，生成多个出库单（一个清理单对应一个仓库编码）
            List<CesClearItem> cesClearItems = cesClear.getItems().stream().filter(a -> StrUtil.isNotEmpty(a.getWarehouseCode())).collect(Collectors.toList());
            Map<String, List<CesClearItem>> groupByWarehouse = cesClearItems.stream().collect(Collectors.groupingBy(CesClearItem::getWarehouseCode));
            groupByWarehouse.forEach((warehouseCode, list) -> {
                List<WarehouseOutAddCmd.Item> itemList = new ArrayList<>();
                WarehouseOutAddCmd warehouseOutAddCmd = WarehouseOutAddCmd.builder().autoCreate(true).
                        warehouseCode(warehouseCode).
                        outDate(LocalDate.now()).
                        handleDate(LocalDateTime.now()).
                        items(itemList).build();
                cmdList.add(warehouseOutAddCmd);
                for (CesClearItem cesClearItem : list) {
                    WarehouseOutAddCmd.Item warehouseOutAddCmdItem = assembler.toWarehouseOutAddCmdItem(cesClearItem);
                    itemList.add(warehouseOutAddCmdItem);
                }
            });
        }
        return cmdList;
    }

    @Transactional
    @Override
    public void remove(CesClearRemoveCmd removeCmd) {
        log.info("CesClearRemoveCmd params:{}", removeCmd);
        cesClearRepository.remove(removeCmd.getSerialNos());
    }

}
