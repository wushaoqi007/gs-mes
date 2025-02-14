package com.greenstone.mes.material.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.enums.BizError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.redis.service.RedisService;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.material.application.dto.PartStockNumberEditCmd;
import com.greenstone.mes.material.application.dto.StockOperationCommand;
import com.greenstone.mes.material.application.dto.StockTransferVo;
import com.greenstone.mes.material.application.helper.StockHelper;
import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.domain.service.StockService;
import com.greenstone.mes.material.infrastructure.enums.BillOperation;
import com.greenstone.mes.material.infrastructure.enums.WarehouseStage;
import com.greenstone.mes.material.infrastructure.enums.StockAction;
import com.greenstone.mes.material.application.service.MaterialStockManager;
import com.greenstone.mes.material.domain.service.IBaseMaterialService;
import com.greenstone.mes.material.domain.service.IBaseWarehouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.Objects;

@Slf4j
@Validated
@Service
public class MaterialStockManagerImpl implements MaterialStockManager {

    private IBaseWarehouseService warehouseService;

    private IBaseMaterialService materialService;

    private StockService stockService;

    private StockHelper stockHelper;

    public RedisService redisService;


    public MaterialStockManagerImpl(IBaseWarehouseService warehouseService, StockService stockService,
                                    StockHelper stockHelper, IBaseMaterialService materialService,
                                    RedisService redisService) {
        this.warehouseService = warehouseService;
        this.stockService = stockService;
        this.stockHelper = stockHelper;
        this.materialService = materialService;
        this.redisService = redisService;
    }

    @Override
    public void transfer(@Valid StockTransferVo transferVo) {
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
                for (StockTransferVo.MaterialInfo materialInfo : transferVo.getMaterialInfoList()) {
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
                    StockOperationCommand stockOperationCommand = stockHelper.toTransferOperationCommand(transferVo, StockAction.TRANSFER,
                            outStockWarehouse, inStockWarehouse);
                    stockService.operation(stockOperationCommand);
                } else if (outStockWarehouse != null) {
                    // 执行出入库操作
                    StockOperationCommand stockOperationCommand = stockHelper.toOperationCommand(transferVo, StockAction.OUT, outStockWarehouse);
                    stockService.operation(stockOperationCommand);
                } else if (inStockWarehouse != null) {
                    // 执行入库操作
                    StockOperationCommand stockOperationCommand = stockHelper.toOperationCommand(transferVo, StockAction.IN, inStockWarehouse);
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

    @Transactional
    @Override
    public void updateStockNumber(PartStockNumberEditCmd editCmd) {
        for (PartStockNumberEditCmd.Material material : editCmd.getMaterialList()) {
            // 检验物料
            BaseMaterial baseMaterial = materialService.getOneOnly(BaseMaterial.builder().code(material.getPartCode()).version(material.getPartVersion()).build());
            if (Objects.isNull(baseMaterial)) {
                throw new ServiceException(BizError.E20001);
            }
            // 校验仓库
            BaseWarehouse warehouse = warehouseService.selectBaseWarehouseById(material.getWarehouseId());
            if (Objects.isNull(warehouse)) {
                throw new ServiceException(BizError.E23001);
            }
            material.setMaterial(baseMaterial);
            material.setWarehouse(warehouse);
        }
        stockService.updateStockNumber(editCmd);
    }


}
