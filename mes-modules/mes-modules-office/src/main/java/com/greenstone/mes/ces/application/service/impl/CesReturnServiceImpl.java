package com.greenstone.mes.ces.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.ces.application.assembler.CesReturnAssembler;
import com.greenstone.mes.ces.application.dto.cmd.CesReturnAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.CesReturnRemoveCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseInAddCmd;
import com.greenstone.mes.ces.application.dto.event.CesReturnAddE;
import com.greenstone.mes.ces.application.dto.query.CesReturnFuzzyQuery;
import com.greenstone.mes.ces.application.dto.result.CesReturnItemResult;
import com.greenstone.mes.ces.application.dto.result.CesReturnResult;
import com.greenstone.mes.ces.application.event.CesReturnAddEvent;
import com.greenstone.mes.ces.application.service.CesReturnService;
import com.greenstone.mes.ces.domain.entity.CesReturn;
import com.greenstone.mes.ces.domain.entity.CesReturnItem;
import com.greenstone.mes.ces.domain.repository.CesReturnRepository;
import com.greenstone.mes.ces.domain.repository.ItemSpecRepository;
import com.greenstone.mes.common.core.enums.FormError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.common.utils.DateUtil;
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

@Slf4j
@Service
@AllArgsConstructor
public class CesReturnServiceImpl implements CesReturnService {

    private final CesReturnRepository cesReturnRepository;
    private final CesReturnAssembler assembler;
    private final RemoteSystemService systemService;
    private final ApplicationEventPublisher eventPublisher;
    private final ItemSpecRepository itemSpecRepository;

    @Override
    public List<CesReturnResult> list(CesReturnFuzzyQuery query) {
        log.info("CesReturnFuzzyQuery params:{}", query);
        List<CesReturn> cesReturns = cesReturnRepository.list(query);
        return assembler.toCesReturnRs(cesReturns);
    }

    @Override
    public CesReturnResult detail(String serialNo) {
        log.info("detail params:{}", serialNo);
        CesReturn cesReturn = cesReturnRepository.detail(serialNo);
        return assembler.toCesReturnR(cesReturn);
    }


    @Override
    public void add(CesReturnAddCmd addCmd) {
        log.info("CesReturnAddCmd params:{}", addCmd);
        CesReturn cesReturn = assembler.toCesReturn(addCmd);
        SerialNoNextCmd nextCmd =
                SerialNoNextCmd.builder().type("ces_return").prefix("CRT" + DateUtil.dateSerialStrNow()).build();
        SerialNoR serialNoR = systemService.getNextSn(nextCmd);

        cesReturn.setSerialNo(serialNoR.getSerialNo());
        cesReturn.setReturnById(SecurityUtils.getLoginUser().getUser().getUserId());
        cesReturn.setReturnByName(SecurityUtils.getLoginUser().getUser().getNickName());
        cesReturn.setReturnByNo(SecurityUtils.getLoginUser().getUser().getEmployeeNo());
        // 校验：去除自定义物品，只能选物品档案中物品
        for (CesReturnItem item : cesReturn.getItems()) {
            if (!itemSpecRepository.existByItemCode(item.getItemCode())) {
                throw new ServiceException(FormError.E70105);
            }
        }
        log.info("add CesReturn params:{}", cesReturn);
        cesReturnRepository.add(cesReturn);

        eventPublisher.publishEvent(new CesReturnAddEvent(CesReturnAddE.builder().serialNo(cesReturn.getSerialNo()).build()));
    }


    @Override
    public List<WarehouseInAddCmd> createWarehouseIn(String serialNo) {
        List<WarehouseInAddCmd> cmdList = new ArrayList<>();
        CesReturn cesReturn = cesReturnRepository.detail(serialNo);
        if (Objects.nonNull(cesReturn)) {
            // 收货单物品按照收货仓库分类，生成多个入库单（一个入库单对应一个仓库编码）
            List<CesReturnItem> cesReturnItems = cesReturn.getItems().stream().filter(a -> StrUtil.isNotEmpty(a.getWarehouseCode())).collect(Collectors.toList());
            Map<String, List<CesReturnItem>> groupByWarehouse = cesReturnItems.stream().collect(Collectors.groupingBy(CesReturnItem::getWarehouseCode));
            groupByWarehouse.forEach((warehouseCode, list) -> {
                List<WarehouseInAddCmd.Item> itemList = new ArrayList<>();
                WarehouseInAddCmd warehouseInAddCmd = WarehouseInAddCmd.builder().autoCreate(true).
                        warehouseCode(warehouseCode).
                        inDate(LocalDate.now()).
                        handleDate(LocalDateTime.now()).
                        items(itemList).build();
                cmdList.add(warehouseInAddCmd);
                for (CesReturnItem cesReturnItem : list) {
                    WarehouseInAddCmd.Item warehouseInAddCmdItem = assembler.toWarehouseInAddCmdItem(cesReturnItem);
                    itemList.add(warehouseInAddCmdItem);
                }
            });
        }
        return cmdList;
    }

    @Override
    public List<CesReturnItemResult> itemList(CesReturnFuzzyQuery query) {
        log.info("CesReturnFuzzyQuery params:{}", query);
        return cesReturnRepository.listItem(query);
    }

    @Transactional
    @Override
    public void remove(CesReturnRemoveCmd removeCmd) {
        log.info("CesReturnRemoveCmd params:{}", removeCmd);
        cesReturnRepository.remove(removeCmd.getSerialNos());
    }

}
