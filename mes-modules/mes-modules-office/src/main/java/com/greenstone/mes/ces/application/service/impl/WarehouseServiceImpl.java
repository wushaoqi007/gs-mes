package com.greenstone.mes.ces.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.enums.WarehouseError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.ces.application.assembler.WarehouseAssembler;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseEditCmd;
import com.greenstone.mes.ces.application.dto.event.WarehouseUpdateE;
import com.greenstone.mes.ces.application.dto.query.WarehouseFuzzyQuery;
import com.greenstone.mes.ces.application.dto.query.WarehouseQuery;
import com.greenstone.mes.ces.application.dto.result.WarehouseResult;
import com.greenstone.mes.ces.application.event.WarehouseUpdateEvent;
import com.greenstone.mes.ces.application.service.WarehouseService;
import com.greenstone.mes.ces.domain.entity.Warehouse;
import com.greenstone.mes.ces.domain.repository.WarehouseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseAssembler warehouseAssembler;
    private final ApplicationEventPublisher eventPublisher;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository, WarehouseAssembler warehouseAssembler, ApplicationEventPublisher eventPublisher) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseAssembler = warehouseAssembler;
        this.eventPublisher = eventPublisher;
    }


    @Override
    public List<WarehouseResult> list(WarehouseQuery query) {
        log.info("WarehouseQuery params:{}", query);
        return warehouseAssembler.toWarehouseResultS(warehouseRepository.listByParentCode(query.getParentWarehouseCode()));
    }

    @Override
    public List<WarehouseResult> search(WarehouseFuzzyQuery query) {
        log.info("WarehouseFuzzyQuery params:{}", query);
        return warehouseAssembler.toWarehouseResultS(warehouseRepository.list(query));
    }

    @Override
    public void add(WarehouseAddCmd addCmd) {
        log.info("WarehouseAddCmd params:{}", addCmd);
        // 校验：仓库类型是否存在
        Warehouse warehouse = warehouseAssembler.toWarehouse(addCmd);
        insertWarehouse(warehouse, true);
    }

    @Override
    public void edit(WarehouseEditCmd editCmd) {
        log.info("WarehouseEditCmd params:{}", editCmd);
        Warehouse warehouse = warehouseAssembler.toWarehouse(editCmd);
        insertWarehouse(warehouse, false);
    }

    public void insertWarehouse(Warehouse warehouse, boolean isNew) {
        log.info("insertWarehouse params:{}.is new:{}", warehouse, isNew);
        // 校验：不允许新增已使用的编码
        Warehouse warehouseWithCodeUsed = warehouseRepository.selectByWarehouseCode(warehouse.getWarehouseCode());
        boolean codeHasBeenUsed = warehouseWithCodeUsed != null;
        if (isNew && codeHasBeenUsed) {
            throw new ServiceException(WarehouseError.E110105);
        }
        Warehouse parentWarehouse = null;
        boolean haveParent = StrUtil.isNotEmpty(warehouse.getParentWarehouseCode());
        if (haveParent) {
            // 校验：父仓库不能是自身
            if (warehouse.getWarehouseCode().equals(warehouse.getParentWarehouseCode())) {
                throw new ServiceException(WarehouseError.E110111);
            }
            // 校验：上级节点是否存在
            parentWarehouse = warehouseRepository.selectByWarehouseCode(warehouse.getParentWarehouseCode());
            if (parentWarehouse == null) {
                throw new ServiceException(WarehouseError.E110104);
            }
        }

        // 校验：更新校验
        Warehouse warehouseExist = null;
        if (!isNew) {
            warehouseExist = warehouseRepository.getById(warehouse.getId());
            if (warehouseExist == null) {
                throw new ServiceException(WarehouseError.E110101);
            }
            // 校验：不允许更新为已使用的仓库编码
            boolean codeUsedByAnOther = codeHasBeenUsed && !warehouse.getId().equals(warehouseWithCodeUsed.getId());
            if (codeUsedByAnOther) {
                throw new ServiceException(WarehouseError.E110105);
            }
            // 校验：更新时，不能将仓库移动到末级分类下，如果没有移动结构则不需要校验
            boolean parentChanged = haveParent && !warehouse.getParentWarehouseCode().equals(warehouseExist.getParentWarehouseCode());
            if (parentChanged) {
                if (!warehouseRepository.haveChildren(parentWarehouse.getWarehouseCode())) {
                    throw new ServiceException(WarehouseError.E110106);
                }
            }
        }

        // 保存物品分类
        if (isNew) {
            log.info("save new warehouse params:{}", warehouse);
            warehouseRepository.save(warehouse);
        }

        // 更新物品分类，保存后需要更新层级结构，在这里一起更新了
        setTypeHierarchy(warehouse, parentWarehouse);
        warehouseRepository.save(warehouse);

        if (!isNew) {
            // 更新子分类的信息
            List<Warehouse> children = warehouseRepository.listChildren(warehouseExist.getWarehouseCode());
            if (CollUtil.isNotEmpty(children)) {
                for (Warehouse child : children) {
                    child.setParentWarehouseCode(warehouse.getWarehouseCode());
                    setTypeHierarchy(child, warehouse);
                }
                log.info("update children params:{}", children);
                warehouseRepository.updateChildren(children);
            }
            // 仓库编码变化更新出入库关联仓库编码、库存关联的仓库编码
            if (!warehouseExist.getWarehouseCode().equals(warehouse.getWarehouseCode())) {
                eventPublisher.publishEvent(new WarehouseUpdateEvent(WarehouseUpdateE.builder().
                        fromWarehouseCode(warehouseExist.getWarehouseCode()).toWarehouseCode(warehouse.getWarehouseCode()).build()));
            }
        }

    }

    @Override
    public void remove(String warehouseCode) {
        log.info("remove warehouse params:{}", warehouseCode);
        // 校验：仓库是否存在
        Warehouse warehouse = warehouseRepository.selectByWarehouseCode(warehouseCode);
        if (warehouse == null) {
            throw new ServiceException(WarehouseError.E110109);
        }
        boolean haveChildren = warehouseRepository.haveChildren(warehouse.getWarehouseCode());
        // 只能删除末级
        if (haveChildren) {
            throw new ServiceException(WarehouseError.E110110);
        }
        warehouseRepository.remove(warehouse.getId());
    }

    private void setTypeHierarchy(Warehouse self, Warehouse parent) {
        String idHierarchy = (parent == null) ? String.valueOf(self.getId()) : parent.getIdHierarchy() + "|" + self.getId();
        self.setIdHierarchy(idHierarchy);
        String nameHierarchy = (parent == null) ? self.getWarehouseName() : self.getWarehouseName() + " / " + parent.getNameHierarchy();
        self.setNameHierarchy(nameHierarchy);
    }
}
