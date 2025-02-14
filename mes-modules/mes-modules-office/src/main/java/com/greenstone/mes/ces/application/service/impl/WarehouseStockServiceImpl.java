package com.greenstone.mes.ces.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.base.api.RemoteOaService;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.ces.application.assembler.WarehouseStockAssembler;
import com.greenstone.mes.ces.application.dto.event.WarehouseStockE;
import com.greenstone.mes.ces.application.dto.event.WarehouseUpdateE;
import com.greenstone.mes.ces.application.dto.query.WarehouseStockFuzzyQuery;
import com.greenstone.mes.ces.application.dto.result.WarehouseStockResult;
import com.greenstone.mes.ces.application.service.WarehouseStockService;
import com.greenstone.mes.ces.domain.entity.WarehouseStock;
import com.greenstone.mes.ces.domain.entity.WarehouseStockDetail;
import com.greenstone.mes.ces.domain.repository.WarehouseRepository;
import com.greenstone.mes.ces.domain.repository.WarehouseStockRepository;
import com.greenstone.mes.common.core.enums.WarehouseIOError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.oa.request.WxMsgSendCmd;
import com.greenstone.mes.system.api.domain.SysUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author wushaoqi
 * @date 2023-06-05-13:27
 */
@Slf4j
@Service
@AllArgsConstructor
public class WarehouseStockServiceImpl implements WarehouseStockService {

    private final WarehouseStockRepository stockRepository;
    private final WarehouseStockAssembler stockAssembler;
    private final WarehouseRepository warehouseRepository;
    private final RemoteOaService oaService;
    private final RemoteSystemService systemService;

    @Override
    public void transfer(WarehouseStockE stockE) {
        log.info("warehouse stock transfer params:{}", stockE);
        List<WarehouseStock> stockList = stockAssembler.toWarehouseStockTransfers(stockE);
        // 校验仓库
        boolean existWarehouse = warehouseRepository.existByWarehouseCode(stockE.getWarehouseCode());
        if (!existWarehouse) {
            log.error("库存转移失败，仓库不存在：{}", stockE.getWarehouseCode());
        }
        for (WarehouseStock warehouseStock : stockList) {
            // 查询库存
            WarehouseStock stock = stockRepository.getByWarehouseAndItem(stockE.getWarehouseCode(), warehouseStock.getItemCode());
            // 更新库存
            if (Objects.isNull(stock)) {
                switch (stockE.getOperation()) {
                    case IN -> stockRepository.add(warehouseStock);
                    case OUT -> throw new ServiceException(WarehouseIOError.E120105);
                }
            } else {
                long numberAfter = 0;
                switch (stockE.getOperation()) {
                    case IN -> numberAfter = stock.getNumber() + warehouseStock.getNumber();
                    case OUT -> numberAfter = stock.getNumber() - warehouseStock.getNumber();
                }
                if (numberAfter < 0) {
                    throw new ServiceException(WarehouseIOError.E120105);
                } else if (numberAfter == 0) {
                    stockRepository.remove(stock.getId());
                } else {
                    warehouseStock.setNumber(numberAfter);
                    warehouseStock.setId(stock.getId());
                    stockRepository.edit(warehouseStock);
                }
            }
        }
    }

    @Override
    public void checkStock() {
        log.info("开始采购库存检查");
        // 检查库存并提醒
        List<WarehouseStockDetail> warehouseStockDetailList = stockRepository.checkStock();
        if (CollUtil.isNotEmpty(warehouseStockDetailList)) {
            StringBuilder content = new StringBuilder("【库存提醒】物品档案以下物品的库存需前往检查:\r\n");
            for (WarehouseStockDetail warehouseStockDetail : warehouseStockDetailList) {
                content.append(StrUtil.format("{}，库存：{}，安全库存上限：{}，安全库存下限：{};\r\n",
                        warehouseStockDetail.getItemName(), warehouseStockDetail.getNumber() == null ? 0 : warehouseStockDetail.getNumber(),
                        warehouseStockDetail.getMaxSecureStock(), warehouseStockDetail.getMinSecureStock()));
            }
            SysUser sysUser = systemService.getUserInfoByWxUserId("WuShaoQi");
            log.info("发送提醒给{}：{}", content, sysUser.getNickName());
            WxMsgSendCmd msgSendCmd = WxMsgSendCmd.builder()
                    .toUser(List.of(WxMsgSendCmd.WxMsgUser.builder().sysUserId(sysUser.getUserId()).build()))
                    .content(content.toString()).build();
            oaService.sendMsgToWx(msgSendCmd);
        }
    }

    @Override
    public void warehouseUpdateEvent(WarehouseUpdateE eventData) {
        log.info("WarehouseUpdateE params:{}", eventData);
        stockRepository.moveTo(eventData.getFromWarehouseCode(), eventData.getToWarehouseCode());
    }

    @Override
    public List<WarehouseStockResult> list(WarehouseStockFuzzyQuery query) {
        log.info("WarehouseStockFuzzyQuery params:{}", query);
        return stockRepository.list(query);
    }
}
