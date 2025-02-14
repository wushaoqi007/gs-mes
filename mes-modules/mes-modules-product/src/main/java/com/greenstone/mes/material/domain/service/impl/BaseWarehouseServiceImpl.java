package com.greenstone.mes.material.domain.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.common.core.enums.SysError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.material.application.dto.cmd.WarehouseImportCmd;
import com.greenstone.mes.material.application.dto.cmd.WhQrcodePrintCmd;
import com.greenstone.mes.material.application.helper.WarehouseHelper;
import com.greenstone.mes.material.application.service.BaseWarehouseManager;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.domain.service.IBaseWarehouseService;
import com.greenstone.mes.material.enums.WarehouseType;
import com.greenstone.mes.material.infrastructure.enums.WarehouseStage;
import com.greenstone.mes.material.infrastructure.mapper.BaseWarehouseMapper;
import com.greenstone.mes.material.request.WarehouseBindProjectCmd;
import com.greenstone.mes.material.request.WarehouseBindReq;
import com.greenstone.mes.material.request.WarehouseUnbindReq;
import com.greenstone.mes.system.api.domain.SysFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

/**
 * 仓库配置Service业务层处理
 *
 * @author gu_renkai
 * @date 2022-01-21
 */
@Service
public class BaseWarehouseServiceImpl extends ServiceImpl<BaseWarehouseMapper, BaseWarehouse> implements IBaseWarehouseService {

    @Autowired
    private BaseWarehouseMapper baseWarehouseMapper;

    @Autowired
    private BaseWarehouseManager baseWarehouseManager;

    @Autowired
    private WarehouseHelper warehouseHelper;

    /**
     * 查询仓库配置
     *
     * @param id 仓库配置主键
     * @return 仓库配置
     */
    @Override
    public BaseWarehouse selectBaseWarehouseById(Long id) {
        return baseWarehouseMapper.selectBaseWarehouseById(id);
    }

    @Override
    public BaseWarehouse findOnlyOneByStage(Integer stage) {
        List<BaseWarehouse> baseWarehouses = baseWarehouseMapper.list(BaseWarehouse.builder().stage(stage).build());
        if (CollUtil.isNotEmpty(baseWarehouses)) {
            if (baseWarehouses.size() > 1) {
                throw new ServiceException(SysError.E10002, StrFormatter.format("found not only one warehouse with stage {}", stage));
            } else {
                return baseWarehouses.get(0);
            }
        }
        return null;
    }

    @Override
    public BaseWarehouse queryWarehouseByCode(BaseWarehouse baseWarehouse) {
        return getOneOnly(baseWarehouse);
    }

    @Override
    public List<BaseWarehouse> queryWarehouseList(BaseWarehouse baseWarehouse) {
        return list(Wrappers.query(baseWarehouse));
    }

    /**
     * 查询仓库配置列表
     *
     * @param baseWarehouse 仓库配置
     * @return 仓库配置
     */
    @Override
    public List<BaseWarehouse> selectBaseWarehouseList(BaseWarehouse baseWarehouse) {
        return baseWarehouseMapper.selectBaseWarehouseList(baseWarehouse);
    }

    /**
     * 新增仓库配置
     *
     * @param baseWarehouse 仓库配置
     * @return 结果
     */
    @Override
    public BaseWarehouse insertBaseWarehouse(BaseWarehouse baseWarehouse) {
        if (Objects.isNull(baseWarehouse.getParentId())) {
            baseWarehouse.setParentId(0L);
        }
        // 砧板只有以下阶段：2（收货）、5（待表处）、6（待返工）、9（良品）
        if (baseWarehouse.getType() == WarehouseType.BOARD.getType() && !WarehouseStage.getById(baseWarehouse.getStage()).isBoardStage()) {
            throw new ServiceException("禁止新增此区域的砧板");
        }
        BaseWarehouse checkEntity = BaseWarehouse.builder().code(baseWarehouse.getCode()).build();
        duplicatedCheck(checkEntity, "existing.warehouse.with.same.code");
        super.save(baseWarehouse);
        return baseWarehouse;
    }

    /**
     * 修改仓库配置
     *
     * @param baseWarehouse 仓库配置
     * @return 结果
     */
    @Override
    public int updateBaseWarehouse(BaseWarehouse baseWarehouse) {
        return baseWarehouseMapper.updateBaseWarehouse(baseWarehouse);
    }

    /**
     * 批量删除仓库配置
     *
     * @param ids 需要删除的仓库配置主键
     * @return 结果
     */
    @Override
    public int deleteBaseWarehouseByIds(Long[] ids) {
        // 检验是否为存放点
        baseWarehouseManager.checkWarehouse(ids);
        return baseWarehouseMapper.deleteBaseWarehouseByIds(ids);
    }

    /**
     * 删除仓库配置信息
     *
     * @param id 仓库配置主键
     * @return 结果
     */
    @Override
    public int deleteBaseWarehouseById(Long id) {
        return baseWarehouseMapper.deleteBaseWarehouseById(id);
    }

    @Override
    public BaseWarehouse bindWarehouse(WarehouseBindReq bindReq) {
        return baseWarehouseManager.bindWarehouse(bindReq);
    }

    @Override
    public void unBindWarehouse(WarehouseUnbindReq unbindReq) {
        BaseWarehouse baseWarehouse = selectBaseWarehouseById(unbindReq.getId());
        if (baseWarehouse.getType() == 1) {
            removeById(unbindReq.getId());
        }
    }

    @Override
    public BaseWarehouse bindProject(WarehouseBindProjectCmd bindProjectCmd) {
        if (StrUtil.isEmpty(bindProjectCmd.getProjectCode())) {
            throw new ServiceException("项目代码不为空");
        }
        return baseWarehouseManager.bindProject(bindProjectCmd);
    }

    @Override
    public void unBindProject(WarehouseBindProjectCmd bindProjectCmd) {
        baseWarehouseManager.unBindProject(bindProjectCmd);
    }

    @Override
    public SysFile printQrCode(WhQrcodePrintCmd printCmd) {
        LambdaQueryWrapper<BaseWarehouse> queryWrapper = Wrappers.lambdaQuery(BaseWarehouse.class).in(BaseWarehouse::getCode, printCmd.getQrCodes());
        List<BaseWarehouse> warehouses = list(queryWrapper);
        List<BufferedImage> qrCodeImages = warehouseHelper.getQrCodeImages(warehouses);
        return warehouseHelper.genQrcodePdf(qrCodeImages, printCmd.getLength(), printCmd.getWidth());
    }

    @Override
    public void importWarehouse(List<WarehouseImportCmd> importList) {
        for (WarehouseImportCmd importCmd : importList) {
            BaseWarehouse warehouse = baseWarehouseMapper.getOneOnly(BaseWarehouse.builder().code(importCmd.getCode()).build());
            if (warehouse == null) {
                BaseWarehouse baseWarehouse = BaseWarehouse.builder().code(importCmd.getCode())
                        .name(importCmd.getCode())
                        .type(WarehouseType.getByName(importCmd.getType()).getType())
                        .stage(WarehouseStage.getByName(importCmd.getStage()).getId())
                        .parentId(0L)
                        .build();
                baseWarehouseMapper.insert(baseWarehouse);
            } else {
                BaseWarehouse baseWarehouse = BaseWarehouse.builder()
                        .id(warehouse.getId())
                        .type(WarehouseType.getByName(importCmd.getType()).getType())
                        .stage(WarehouseStage.getByName(importCmd.getStage()).getId()).build();
                baseWarehouseMapper.updateById(baseWarehouse);
            }
        }
    }

}