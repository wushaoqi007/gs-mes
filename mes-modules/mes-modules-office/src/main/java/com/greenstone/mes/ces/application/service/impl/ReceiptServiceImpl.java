package com.greenstone.mes.ces.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.ces.application.assembler.ReceiptAssembler;
import com.greenstone.mes.ces.application.dto.cmd.ReceiptAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.ReceiptEditCmd;
import com.greenstone.mes.ces.application.dto.cmd.ReceiptRemoveCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseInAddCmd;
import com.greenstone.mes.ces.application.dto.query.ReceiptFuzzyQuery;
import com.greenstone.mes.ces.application.dto.result.ReceiptResult;
import com.greenstone.mes.ces.application.event.ReceiptAddEvent;
import com.greenstone.mes.ces.application.service.ReceiptService;
import com.greenstone.mes.ces.domain.entity.ItemSpec;
import com.greenstone.mes.ces.domain.entity.Receipt;
import com.greenstone.mes.ces.domain.entity.ReceiptItem;
import com.greenstone.mes.ces.domain.repository.ItemSpecRepository;
import com.greenstone.mes.ces.domain.repository.ReceiptRepository;
import com.greenstone.mes.ces.dto.cmd.ReceiptStatusChangeCmd;
import com.greenstone.mes.common.core.enums.FormError;
import com.greenstone.mes.common.core.enums.ReceiptError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.external.application.service.ProcessInstanceService;
import com.greenstone.mes.external.dto.cmd.ProcessStartCmd;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.form.dto.cmd.ProcessResult;
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

/**
 * @author wushaoqi
 * @date 2023-05-25-14:19
 */
@Slf4j
@Service
@AllArgsConstructor
public class ReceiptServiceImpl implements ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final ReceiptAssembler assembler;
    private final RemoteSystemService systemService;
    private final ProcessInstanceService flowService;
    private final ApplicationEventPublisher eventPublisher;
    private final ItemSpecRepository itemSpecRepository;

    @Override
    public List<ReceiptResult> list(ReceiptFuzzyQuery query) {
        log.info("ReceiptFuzzyQuery params:{}", query);
        List<Receipt> receipts = receiptRepository.list(query);
        return assembler.toReceiptRs(receipts);
    }

    @Override
    public ReceiptResult detail(String serialNo) {
        log.info("detail params:{}", serialNo);
        Receipt receipt = receiptRepository.detail(serialNo);
        return assembler.toReceiptR(receipt);
    }


    @Override
    public void add(ReceiptAddCmd addCmd) {
        log.info("ReceiptAddCmd params:{}", addCmd);
        Receipt receipt = assembler.toReceipt(addCmd);
        SerialNoNextCmd nextCmd =
                SerialNoNextCmd.builder().type("purchase_receipt_manage").prefix("PRM" + DateUtil.dateSerialStrNow()).build();
        SerialNoR serialNoR = systemService.getNextSn(nextCmd);

        receipt.setSerialNo(serialNoR.getSerialNo());
        receipt.setStatus(addCmd.isCommit() ? ProcessStatus.APPROVING : ProcessStatus.DRAFT);
        receipt.setReceiveBy(SecurityUtils.getLoginUser().getUser().getUserId());
        receipt.setReceiveByName(SecurityUtils.getLoginUser().getUser().getNickName());
        // 校验：去除自定义物品，只能选物品档案中物品
        for (ReceiptItem item : receipt.getItems()) {
            if (!itemSpecRepository.existByItemCode(item.getItemCode())) {
                throw new ServiceException(FormError.E70105);
            }
        }
        log.info("add Receipt params:{}", receipt);
        receiptRepository.add(receipt);

        if (addCmd.isCommit()) {
            ProcessStartCmd startCmd = ProcessStartCmd.builder().formId("purchase_receipt").serialNo(serialNoR.getSerialNo()).build();
            log.info("commit params:{}", startCmd);
            flowService.createAndRun(startCmd);
        }
    }

    @Transactional
    @Override
    public void edit(ReceiptEditCmd editCmd) {
        log.info("ReceiptEditCmd params:{}", editCmd);
        Receipt receiptFound = receiptRepository.get(editCmd.getSerialNo());
        if (receiptFound == null) {
            throw new ServiceException(ReceiptError.E100101);
        }
        if (receiptFound.getStatus() != ProcessStatus.DRAFT) {
            throw new ServiceException(ReceiptError.E100102);
        }

        if (editCmd.isCommit()) {
            ProcessStartCmd startCmd = ProcessStartCmd.builder().formId("purchase_receipt").serialNo(editCmd.getSerialNo()).build();
            flowService.createAndRun(startCmd);
        }
        Receipt receipt = assembler.toReceipt(editCmd);
        receipt.setStatus(editCmd.isCommit() ? ProcessStatus.APPROVING : ProcessStatus.DRAFT);
        for (ReceiptItem item : receipt.getItems()) {
            item.setSerialNo(editCmd.getSerialNo());
            if (!itemSpecRepository.existByItemCode(item.getItemCode())) {
                throw new ServiceException(FormError.E70105);
            }
        }
        log.info("edit Receipt params:{}", receipt);
        receiptRepository.edit(receipt);
    }

    @Override
    public void statusChange(ReceiptStatusChangeCmd statusChangeCmd) {
        log.info("ReceiptStatusChangeCmd params:{}", statusChangeCmd);
        if (statusChangeCmd.getStatus() == ProcessStatus.COMMITTED) {
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                Receipt receiptFound = receiptRepository.get(serialNo);
                if (receiptFound == null) {
                    throw new ServiceException(ReceiptError.E100101);
                }
                if (receiptFound.getStatus() != ProcessStatus.DRAFT) {
                    throw new ServiceException(ReceiptError.E100102);
                }
                receiptRepository.changeStatus(Receipt.builder().serialNo(serialNo).status(statusChangeCmd.getStatus()).build());
                ProcessStartCmd startCmd = ProcessStartCmd.builder().formId("purchase_receipt").serialNo(serialNo).build();
                flowService.createAndRun(startCmd);
            }
        } else if (statusChangeCmd.getStatus() == ProcessStatus.CLOSED) {
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                Receipt receiptFound = receiptRepository.get(serialNo);
                if (receiptFound == null) {
                    throw new ServiceException(ReceiptError.E100101);
                }
                if (receiptFound.getStatus() != ProcessStatus.APPROVED) {
                    throw new ServiceException(ReceiptError.E100103);
                }
            }
            receiptRepository.statusChange(statusChangeCmd);
        } else if (statusChangeCmd.getStatus() == ProcessStatus.APPROVED) {
            for (String serialNo : statusChangeCmd.getSerialNos()) {
                Receipt receiptFound = receiptRepository.detail(serialNo);
                if (receiptFound == null) {
                    throw new ServiceException(ReceiptError.E100101);
                }
                receiptRepository.changeStatus(Receipt.builder().serialNo(serialNo).status(statusChangeCmd.getStatus()).build());
                // 防止多次点击
                if (receiptFound.getStatus() != ProcessStatus.APPROVED) {
                    eventPublisher.publishEvent(new ReceiptAddEvent(assembler.toReceiptAddEvent(receiptFound)));
                }
            }
        } else {
            receiptRepository.statusChange(statusChangeCmd);
        }
    }

    @Override
    public List<WarehouseInAddCmd> createWarehouseIn(String serialNo) {
        List<WarehouseInAddCmd> cmdList = new ArrayList<>();
        Receipt receipt = receiptRepository.detail(serialNo);
        if (Objects.nonNull(receipt)) {
            // 收货单物品按照收货仓库分类，生成多个入库单（一个入库单对应一个仓库编码）
            List<ReceiptItem> receiptItems = receipt.getItems().stream().filter(a -> StrUtil.isNotEmpty(a.getWarehouseCode())).collect(Collectors.toList());
            Map<String, List<ReceiptItem>> groupByWarehouse = receiptItems.stream().collect(Collectors.groupingBy(ReceiptItem::getWarehouseCode));
            groupByWarehouse.forEach((warehouseCode, list) -> {
                List<WarehouseInAddCmd.Item> itemList = new ArrayList<>();
                WarehouseInAddCmd warehouseInAddCmd = WarehouseInAddCmd.builder().autoCreate(true).
                        warehouseCode(warehouseCode).
                        inDate(LocalDate.now()).
                        handleDate(LocalDateTime.now()).
                        items(itemList).build();
                cmdList.add(warehouseInAddCmd);
                for (ReceiptItem receiptItem : list) {
                    WarehouseInAddCmd.Item warehouseInAddCmdItem = assembler.toWarehouseInAddCmdItem(receiptItem);
                    warehouseInAddCmdItem.setReceiptSerialNo(serialNo);
                    if (receiptItem.getItemCode() != null) {
                        ItemSpec itemSpec = itemSpecRepository.detail(receiptItem.getItemCode());
                        if (itemSpec != null) {
                            warehouseInAddCmdItem.setTypeName(itemSpec.getTypeName());
                        }
                    }
                    itemList.add(warehouseInAddCmdItem);
                }
            });
        }
        return cmdList;
    }

    @Override
    public void approved(ProcessResult processResult) {
        Receipt changeCmd = Receipt.builder().serialNo(processResult.getSerialNo())
                .status(ProcessStatus.APPROVED).build();
        receiptRepository.changeStatus(changeCmd);
    }

    @Transactional
    @Override
    public void remove(ReceiptRemoveCmd removeCmd) {
        log.info("ReceiptRemoveCmd params:{}", removeCmd);
        receiptRepository.remove(removeCmd.getSerialNos());
    }

}
