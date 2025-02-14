package com.greenstone.mes.material.domain.service;

import com.greenstone.mes.material.application.dto.cmd.WarehouseImportCmd;
import com.greenstone.mes.material.application.dto.cmd.WhQrcodePrintCmd;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.request.WarehouseBindProjectCmd;
import com.greenstone.mes.material.request.WarehouseBindReq;
import com.greenstone.mes.material.request.WarehouseUnbindReq;
import com.greenstone.mes.common.mybatisplus.IServiceWrapper;
import com.greenstone.mes.system.api.domain.SysFile;

import java.util.List;

/**
 * 仓库配置Service接口
 *
 * @author gu_renkai
 * @date 2022-01-21
 */
public interface IBaseWarehouseService extends IServiceWrapper<BaseWarehouse> {
    /**
     * 查询仓库配置
     *
     * @param id 仓库配置主键
     * @return 仓库配置
     */
    BaseWarehouse selectBaseWarehouseById(Long id);

    BaseWarehouse findOnlyOneByStage(Integer id);

    BaseWarehouse queryWarehouseByCode(BaseWarehouse baseWarehouse);

    List<BaseWarehouse> queryWarehouseList(BaseWarehouse baseWarehouse);

    /**
     * 查询仓库配置列表
     *
     * @param baseWarehouse 仓库配置
     * @return 仓库配置集合
     */
    List<BaseWarehouse> selectBaseWarehouseList(BaseWarehouse baseWarehouse);

    /**
     * 新增仓库配置
     *
     * @param baseWarehouse 仓库配置
     * @return 结果
     */
    BaseWarehouse insertBaseWarehouse(BaseWarehouse baseWarehouse);

    /**
     * 修改仓库配置
     *
     * @param baseWarehouse 仓库配置
     * @return 结果
     */
    int updateBaseWarehouse(BaseWarehouse baseWarehouse);

    /**
     * 批量删除仓库配置
     *
     * @param ids 需要删除的仓库配置主键集合
     * @return 结果
     */
    int deleteBaseWarehouseByIds(Long[] ids);

    /**
     * 删除仓库配置信息
     *
     * @param id 仓库配置主键
     * @return 结果
     */
    int deleteBaseWarehouseById(Long id);

    /**
     * 存放点绑定仓库
     *
     * @param bindReq
     * @return 结果
     */
    BaseWarehouse bindWarehouse(WarehouseBindReq bindReq);

    /**
     * 解绑
     *
     * @param unbindReq
     * @return
     */
    void unBindWarehouse(WarehouseUnbindReq unbindReq);

    BaseWarehouse bindProject(WarehouseBindProjectCmd bindProjectCmd);

    void unBindProject(WarehouseBindProjectCmd bindProjectCmd);

    SysFile printQrCode(WhQrcodePrintCmd printCmd);

    void importWarehouse(List<WarehouseImportCmd> importList);

}