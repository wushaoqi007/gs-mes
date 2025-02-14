package com.greenstone.mes.machine.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineStockRecord;
import com.greenstone.mes.material.request.StockRecordDetailListReq;
import com.greenstone.mes.material.request.StockRecordMaterialSearchReq;
import com.greenstone.mes.material.request.StockRecordSearchReq;
import com.greenstone.mes.material.response.StockRecordDetailListResp;
import com.greenstone.mes.material.response.StockRecordMaterialSearchResp;
import com.greenstone.mes.material.response.StockRecordSearchResp;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 物料出入库记录Mapper接口
 *
 * @author gu_renkai
 * @date 2022-02-17
 */
@Repository
public interface MachineStockRecordMapper extends BaseMapper<MachineStockRecord> {
    /**
     * 查询物料出入库记录
     *
     * @param id 物料出入库记录主键
     * @return 物料出入库记录
     */
    MachineStockRecord selectMachineStockRecordById(Long id);

    /**
     * 查询物料出入库记录列表
     *
     * @param machineStockRecord 物料出入库记录
     * @return 物料出入库记录集合
     */
    List<MachineStockRecord> selectMachineStockRecordList(MachineStockRecord machineStockRecord);

    /**
     * 新增物料出入库记录
     *
     * @param machineStockRecord 物料出入库记录
     * @return 结果
     */
    int insertMachineStockRecord(MachineStockRecord machineStockRecord);

    /**
     * 修改物料出入库记录
     *
     * @param machineStockRecord 物料出入库记录
     * @return 结果
     */
    int updateMachineStockRecord(MachineStockRecord machineStockRecord);

    /**
     * 删除物料出入库记录
     *
     * @param id 物料出入库记录主键
     * @return 结果
     */
    int deleteMachineStockRecordById(Long id);

    /**
     * 批量删除物料出入库记录
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteMachineStockRecordByIds(Long[] ids);

    List<StockRecordSearchResp> listStockRecord(StockRecordSearchReq searchReq);

    List<StockRecordDetailListResp> listStockRecordDetail(StockRecordDetailListReq req);

    List<StockRecordMaterialSearchResp> listStockRecordMaterial(StockRecordMaterialSearchReq searchReq);
}