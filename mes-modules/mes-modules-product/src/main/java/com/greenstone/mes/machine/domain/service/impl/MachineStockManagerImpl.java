package com.greenstone.mes.machine.domain.service.impl;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.enums.BizError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.redis.service.RedisService;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineStockOperationCommand;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineStockTransferVo;
import com.greenstone.mes.machine.application.helper.MachineStockHelper;
import com.greenstone.mes.machine.application.service.MachineStockService;
import com.greenstone.mes.machine.domain.service.MachineStockManager;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.domain.service.IBaseWarehouseService;
import com.greenstone.mes.material.infrastructure.enums.BillOperation;
import com.greenstone.mes.material.infrastructure.enums.WarehouseStage;
import com.greenstone.mes.material.infrastructure.enums.StockAction;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@AllArgsConstructor
@Slf4j
@Validated
@Service
public class MachineStockManagerImpl implements MachineStockManager {

    private final IBaseWarehouseService warehouseService;

    private final MachineStockService stockService;

    private final MachineStockHelper stockHelper;

    public final RedisService redisService;


    @Override
    public void transfer(@Valid MachineStockTransferVo transferVo) {
        String key = "transferLock" + SecurityUtils.getUserId();
        // 访问Redis锁，是否可以访问（程序执行完，或挂起10分钟后，可访问该接口）
        boolean allowAccess = redisService.setNx(key, String.valueOf(SecurityUtils.getUserId()), 10);
        if (allowAccess) {
            try {
                BillOperation operation = transferVo.getOperation();
                // 校验是否选择了仓库
                Long warehouseId = transferVo.getInStockWhId() == null ? transferVo.getOutStockWhId() : transferVo.getInStockWhId();
                if (warehouseId == null) {
                    throw new ServiceException(BizError.E23010);
                }
                BaseWarehouse warehouse = warehouseService.selectBaseWarehouseById(warehouseId);
                // 校验项目是否指定仓库
                for (MachineStockTransferVo.MaterialInfo materialInfo : transferVo.getMaterialInfoList()) {
                    if (StrUtil.isNotEmpty(warehouse.getProjectCode()) && !materialInfo.getProjectCode().equals(warehouse.getProjectCode())) {
                        throw new ServiceException(BizError.E23011, warehouse.getProjectCode());
                    }
                }
                WarehouseStage stage = WarehouseStage.getById(warehouse.getStage());
                // 校验是否可以进行此阶段的操作
                if (!operation.isValidStages(stage)) {
                    throw new ServiceException(BizError.E23009);
                }

                BaseWarehouse inStockWarehouse = null;
                BaseWarehouse outStockWarehouse = null;

                switch (transferVo.getOperation().getAction()) {
                    case IN -> inStockWarehouse = warehouse;
                    case OUT -> outStockWarehouse = warehouse;
                }

                // 补充默认的出入库仓库的ID，如：表处完成时，从表处中阶段，入库到待检验阶段；
                // 从表处中出库是预设好的，那么出库仓库就默认为是表处中，默认是先出库后入库，用户只需要选择入库的仓库即可
                BillOperation.StageAction stageAction = operation.getDefaultAction();
                if (stageAction != null) {
                    switch (stageAction.getAction()) {
                        case IN -> inStockWarehouse = warehouseService.findOnlyOneByStage(stageAction.getStage().getId());
                        case OUT -> outStockWarehouse = warehouseService.findOnlyOneByStage(stageAction.getStage().getId());
                    }
                }

                if (outStockWarehouse != null && inStockWarehouse != null) {
                    MachineStockOperationCommand stockOperationCommand = stockHelper.toTransferOperationCommand(transferVo, StockAction.TRANSFER,
                            outStockWarehouse, inStockWarehouse);
                    stockService.operation(stockOperationCommand);
                } else if (outStockWarehouse != null) {
                    // 执行出入库操作
                    MachineStockOperationCommand stockOperationCommand = stockHelper.toOperationCommand(transferVo, StockAction.OUT, outStockWarehouse);
                    stockService.operation(stockOperationCommand);
                } else if (inStockWarehouse != null) {
                    // 执行入库操作
                    MachineStockOperationCommand stockOperationCommand = stockHelper.toOperationCommand(transferVo, StockAction.IN, inStockWarehouse);
                    stockService.operation(stockOperationCommand);
                }
            } finally {
                redisService.deleteObject(key);
            }
        } else {
            log.info("零件转移中，请勿重复访问程序：{}", key);
            throw new ServiceException(BizError.E60005);
        }
    }


}
