package com.greenstone.mes.machine.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineStockRecordDetail;
import com.greenstone.mes.material.response.MaterialInfoResp;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 物料出入库记录明细Mapper接口
 *
 * @author gu_renkai
 * @date 2022-02-17
 */
@Repository
public interface MachineStockRecordDetailMapper extends BaseMapper<MachineStockRecordDetail> {

    List<MaterialInfoResp> listStockRecordDetail(Long recordId);


    /**
     * 查询物料出入库记录明细
     *
     * @param id 物料出入库记录明细主键
     * @return 物料出入库记录明细
     */
    MachineStockRecordDetail selectMachineStockRecordDetailById(Long id);

    /**
     * 查询物料出入库记录明细列表
     *
     * @param machineStockRecordDetail 物料出入库记录明细
     * @return 物料出入库记录明细集合
     */
    List<MachineStockRecordDetail> selectMachineStockRecordDetailList(MachineStockRecordDetail machineStockRecordDetail);

    /**
     * 新增物料出入库记录明细
     *
     * @param machineStockRecordDetail 物料出入库记录明细
     * @return 结果
     */
    int insertMachineStockRecordDetail(MachineStockRecordDetail machineStockRecordDetail);

    /**
     * 修改物料出入库记录明细
     *
     * @param machineStockRecordDetail 物料出入库记录明细
     * @return 结果
     */
    int updateMachineStockRecordDetail(MachineStockRecordDetail machineStockRecordDetail);

    /**
     * 删除物料出入库记录明细
     *
     * @param id 物料出入库记录明细主键
     * @return 结果
     */
    int deleteMachineStockRecordDetailById(Long id);

    /**
     * 批量删除物料出入库记录明细
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteMachineStockRecordDetailByIds(Long[] ids);
}