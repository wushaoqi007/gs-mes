package com.greenstone.mes.material.domain.service;

import com.greenstone.mes.common.mybatisplus.IServiceWrapper;
import com.greenstone.mes.material.domain.MaterialSupplier;
import com.greenstone.mes.material.request.MaterialSupplierAddReq;
import com.greenstone.mes.material.request.MaterialSupplierEditReq;
import com.greenstone.mes.material.request.MaterialSupplierListReq;

import java.util.List;

/**
 * 供应商管理接口
 *
 * @author wushaoqi
 * @date 2022-09-26-15:19
 */
public interface IMaterialSupplierService extends IServiceWrapper<MaterialSupplier> {
    /**
     * 查询列表
     *
     * @param supplierListReq 查询条件
     * @return
     */
    List<MaterialSupplier> selectMaterialSupplierList(MaterialSupplierListReq supplierListReq);

    /**
     * 新增供应商
     *
     * @param supplierAddReq
     */
    void insertMaterialSupplier(MaterialSupplierAddReq supplierAddReq);

    /**
     * 修改供应商
     *
     * @param supplierEditReq
     */
    void updateMaterialSupplier(MaterialSupplierEditReq supplierEditReq);

    /**
     * 查询详情
     *
     * @param id
     * @return
     */
    MaterialSupplier selectMaterialSupplierById(Long id);

}
