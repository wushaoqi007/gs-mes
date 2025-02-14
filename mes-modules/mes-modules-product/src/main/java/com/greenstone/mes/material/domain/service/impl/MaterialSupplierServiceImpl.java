package com.greenstone.mes.material.domain.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.material.domain.MaterialSupplier;
import com.greenstone.mes.material.domain.service.IMaterialSupplierService;
import com.greenstone.mes.material.infrastructure.mapper.MaterialSupplierMapper;
import com.greenstone.mes.material.request.MaterialSupplierAddReq;
import com.greenstone.mes.material.request.MaterialSupplierEditReq;
import com.greenstone.mes.material.request.MaterialSupplierListReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 供应商管理Service业务处理
 *
 * @author wushaoqi
 * @date 2022-09-26-15:20
 */
@Slf4j
@Service
public class MaterialSupplierServiceImpl extends ServiceImpl<MaterialSupplierMapper, MaterialSupplier> implements IMaterialSupplierService {

    @Override
    public List<MaterialSupplier> selectMaterialSupplierList(MaterialSupplierListReq supplierListReq) {
        QueryWrapper<MaterialSupplier> queryWrapper = Wrappers.query(MaterialSupplier.builder().build());
        queryWrapper.orderByDesc("create_time");
        if (StrUtil.isNotBlank(supplierListReq.getName())) {
            queryWrapper.like("name", supplierListReq.getName());
        }
        return list(queryWrapper);
    }

    @Override
    public void insertMaterialSupplier(MaterialSupplierAddReq supplierAddReq) {
        // 同名校验
        QueryWrapper<MaterialSupplier> queryWrapper = Wrappers.query(MaterialSupplier.builder().name(supplierAddReq.getName()).build());
        MaterialSupplier check = getOneOnly(queryWrapper);
        if (Objects.nonNull(check)) {
            log.error("供应商名称已存在:" + supplierAddReq.getName());
            throw new ServiceException("供应商名称已存在:" + supplierAddReq.getName());
        }
        // 保存
        MaterialSupplier materialSupplier = MaterialSupplier.builder().name(supplierAddReq.getName()).phone(supplierAddReq.getPhone()).address(supplierAddReq.getAddress()).build();
        save(materialSupplier);
    }

    @Override
    public void updateMaterialSupplier(MaterialSupplierEditReq supplierEditReq) {
        // 查找
        MaterialSupplier oneOnly = getOneOnly(MaterialSupplier.builder().id(supplierEditReq.getId()).build());
        if (Objects.isNull(oneOnly)) {
            log.error("未找到供应商:" + supplierEditReq.getId());
            throw new ServiceException("未找到供应商:" + supplierEditReq.getId());
        }
        // 同名校验
        if(StrUtil.isNotBlank(supplierEditReq.getName())){
            QueryWrapper<MaterialSupplier> queryWrapper = Wrappers.query(MaterialSupplier.builder().name(supplierEditReq.getName()).build());
            queryWrapper.ne("id", supplierEditReq.getId());
            MaterialSupplier check = getOneOnly(queryWrapper);
            if (Objects.nonNull(check)) {
                log.error("供应商名称已存在:" + supplierEditReq.getName());
                throw new ServiceException("供应商名称已存在:" + supplierEditReq.getName());
            }
        }
        // 修改
        MaterialSupplier materialSupplier = MaterialSupplier.builder()
                .id(supplierEditReq.getId()).build();
        if(StrUtil.isNotBlank(supplierEditReq.getName())){
            materialSupplier.setName(supplierEditReq.getName());
        }
        if(StrUtil.isNotBlank(supplierEditReq.getPhone())){
            materialSupplier.setPhone(supplierEditReq.getPhone());
        }
        if(StrUtil.isNotBlank(supplierEditReq.getAddress())){
            materialSupplier.setAddress(supplierEditReq.getAddress());
        }
        updateById(materialSupplier);
    }

    @Override
    public MaterialSupplier selectMaterialSupplierById(Long id) {
        MaterialSupplier oneOnly = getOneOnly(MaterialSupplier.builder().id(id).build());
        if (Objects.isNull(oneOnly)) {
            log.error("未找到供应商:" + id);
            throw new ServiceException("未找到供应商:" + id);
        }
        return oneOnly;
    }

}
