package com.greenstone.mes.material.application.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.base.api.RemoteBomService;
import com.greenstone.mes.bom.response.BomQueryResp;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.material.application.service.MaterialReceivingManager;
import com.greenstone.mes.material.constant.PrefixConstant;
import com.greenstone.mes.material.domain.MaterialReceiving;
import com.greenstone.mes.material.domain.MaterialReceivingDetail;
import com.greenstone.mes.material.domain.MaterialReceivingStatusChange;
import com.greenstone.mes.material.infrastructure.enums.WarehouseStage;
import com.greenstone.mes.material.request.MaterialReceivingAddReq;
import com.greenstone.mes.material.request.MaterialReceivingEditReq;
import com.greenstone.mes.material.request.StockSearchListReq;
import com.greenstone.mes.material.response.MaterialReceivingDetailResp;
import com.greenstone.mes.material.response.MaterialReceivingStatusListResp;
import com.greenstone.mes.material.response.StockListResp;
import com.greenstone.mes.material.domain.service.IMaterialReceivingDetailService;
import com.greenstone.mes.material.domain.service.IMaterialReceivingService;
import com.greenstone.mes.material.domain.service.IMaterialReceivingStatusChangeService;
import com.greenstone.mes.material.domain.service.MaterialStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2022-08-15-8:39
 */
@Slf4j
@Service
public class MaterialReceivingManagerImpl implements MaterialReceivingManager {

    @Autowired
    private IMaterialReceivingService receivingService;

    @Autowired
    private IMaterialReceivingDetailService receivingDetailService;

    @Autowired
    private IMaterialReceivingStatusChangeService receivingStatusChangeService;

    @Autowired
    private RemoteBomService remoteBomService;

    @Autowired
    private MaterialStockService materialStockService;

    @Override
    @Transactional
    public Long insertMaterialReceiving(MaterialReceivingAddReq receivingAddReq) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        // 单号编码规则：固定前缀+项目号+时间
        String code = PrefixConstant.MATERIAL_CODE_PREFIX + receivingAddReq.getProjectCode() + format.format(new Date());
        MaterialReceiving materialReceiving = MaterialReceiving.builder().code(code).projectCode(receivingAddReq.getProjectCode())
                .status(0).deadline(receivingAddReq.getDeadline()).build();
        receivingService.save(materialReceiving);

        // 领料单详情
        if (CollectionUtil.isNotEmpty(receivingAddReq.getBomList())) {
            for (MaterialReceivingAddReq.BomInfo bomInfo : receivingAddReq.getBomList()) {
                // 查找BOM信息
                R<BomQueryResp> bomById = remoteBomService.getBomById(bomInfo.getBomId());
                if (R.FAIL == bomById.getCode()) {
                    log.error("bom未找到,bomId：" + bomInfo.getBomId());
                    throw new ServiceException("bom未找到," + bomById.getMsg());
                }
                BomQueryResp bom = bomById.getData();
                // 领料单详情
                if (CollectionUtil.isNotEmpty(bom.getCompositions())) {
                    for (BomQueryResp.Composition composition : bom.getCompositions()) {
                        // 查询同一领料单下是否有相同物料
                        QueryWrapper<MaterialReceivingDetail> queryWrapper = Wrappers.query(MaterialReceivingDetail.builder()
                                .receivingId(materialReceiving.getId()).materialId(composition.getMaterialId()).build());
                        MaterialReceivingDetail exitOne = receivingDetailService.getOneOnly(queryWrapper);
                        Integer totalNum = composition.getNumber().intValue();
                        // 有则合并数量更新，无则新增
                        if (exitOne != null) {
                            totalNum += exitOne.getTotalNum();
                            exitOne.setTotalNum(totalNum);
                            receivingDetailService.updateById(exitOne);
                        } else {
                            MaterialReceivingDetail materialReceivingDetail =
                                    MaterialReceivingDetail.builder().receivingId(materialReceiving.getId())
                                            .materialId(composition.getMaterialId())
                                            .materialName(composition.getMaterialName()).materialCode(composition.getMaterialCode())
                                            .materialVersion(composition.getMaterialVersion()).totalNum(totalNum)
                                            .receivedNum(0).build();
                            receivingDetailService.save(materialReceivingDetail);
                        }
                    }
                }
            }
        }
        // 状态记录(0待接收、1备料中、2待领料、3已完成、4已关闭)
        receivingStatusChangeService.save(MaterialReceivingStatusChange.builder().receivingId(materialReceiving.getId()).status(0).build());
        return materialReceiving.getId();

    }

    @Override
    public List<MaterialReceivingDetailResp> completeReceivingDetail(List<MaterialReceivingDetailResp> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            for (MaterialReceivingDetailResp materialReceivingDetailResp : list) {
                // 查询库存总数量
                List<StockListResp> stockList = materialStockService.listSearchStock(StockSearchListReq.builder().materialId(materialReceivingDetailResp.getMaterialId()).stage(WarehouseStage.GOOD.getId()).build());
                int stockNum = 0;
                if (CollectionUtil.isNotEmpty(stockList)) {
                    for (StockListResp stockListResp : stockList) {
                        stockNum += Integer.parseInt(stockListResp.getNumber());
                    }
                }
                materialReceivingDetailResp.setTotalStockNum(stockNum);
            }

        }
        // 排序：0剩余出库数量放最下面
        return sortReceiving(list);
    }

    @Override
    public List<MaterialReceivingDetailResp> prepare(Long id) {
        List<MaterialReceivingDetailResp> list = receivingService.selectMaterialReceivingDetailById(id);
        if (CollectionUtil.isNotEmpty(list)) {
            for (MaterialReceivingDetailResp materialReceivingDetailResp : list) {
                // 查询库存总数量
                List<StockListResp> stockList = materialStockService.listSearchStock(StockSearchListReq.builder().materialId(materialReceivingDetailResp.getMaterialId()).stage(WarehouseStage.GOOD.getId()).build());
                List<MaterialReceivingDetailResp.WarehouseInfo> warehouseList = new ArrayList<>();
                if (CollectionUtil.isNotEmpty(stockList)) {
                    for (StockListResp stockListResp : stockList) {
                        // 补充仓库信息
                        MaterialReceivingDetailResp.WarehouseInfo warehouseInfo =
                                MaterialReceivingDetailResp.WarehouseInfo.builder().warehouseId(stockListResp.getWarehouseId())
                                        .warehouseCode(stockListResp.getWarehouseCode())
                                        .warehouseName(stockListResp.getWarehouseName())
                                        .stockNum(Integer.parseInt(stockListResp.getNumber())).build();
                        warehouseList.add(warehouseInfo);
                    }
                    materialReceivingDetailResp.setWarehouseList(warehouseList);
                }
            }

        }
        // 排序：0剩余出库数量放最下面
        return sortReceiving(list);
    }

    public List<MaterialReceivingDetailResp> sortReceiving(List<MaterialReceivingDetailResp> list) {
        Collections.sort(list, (o1, o2) -> {
            int differ1 = o1.getTotalNum() - o1.getReceivedNum();
            int differ2 = o2.getTotalNum() - o2.getReceivedNum();
            if (differ1 == 0) {
                return 1;
            }
            if (differ2 == 0) {
                return -1;
            }
            return 0;
        });
        return list;
    }


    @Override
    @Transactional
    public void updateMaterialReceivingStatus(MaterialReceivingEditReq materialReceivingEditReq) {
        MaterialReceiving materialReceiving = receivingService.getById(materialReceivingEditReq.getId());
        if (ObjectUtil.isEmpty(materialReceiving)) {
            log.error("未找到领料单,id:" + materialReceivingEditReq.getId());
            throw new ServiceException("未找到领料单,id:" + materialReceivingEditReq.getId());
        }
        if (materialReceivingEditReq.getStatus() == null) {
            log.error("状态不为空");
            throw new ServiceException("状态不为空");
        }
        if (materialReceivingEditReq.getStatus() == 3 && !SecurityUtils.getLoginUser().getUser().getNickName().equals(materialReceiving.getReceiveBy())) {
            log.error("仅接收人可完成领料单！");
            throw new ServiceException("仅接收人可完成领料单！");
        }
        if (materialReceivingEditReq.getStatus() == 4 && !SecurityUtils.getLoginUser().getUser().getNickName().equals(materialReceiving.getCreateBy())) {
            log.error("仅创建人可关闭领料单！");
            throw new ServiceException("仅创建人可关闭领料单！");
        }
        materialReceiving.setStatus(materialReceivingEditReq.getStatus());
        receivingService.updateById(materialReceiving);
        // 状态记录(0待接收、1备料中、2待领料、3已完成、4已关闭)
        receivingStatusChangeService.save(MaterialReceivingStatusChange.builder().receivingId(materialReceiving.getId()).status(materialReceivingEditReq.getStatus()).build());
    }

    @Override
    public List<MaterialReceivingStatusListResp> getStatusListById(Long id) {
        List<MaterialReceivingStatusListResp> respList = new ArrayList<>();
        QueryWrapper<MaterialReceivingStatusChange> queryWrapper = Wrappers.query(MaterialReceivingStatusChange.builder().receivingId(id).build());
        List<MaterialReceivingStatusChange> list = receivingStatusChangeService.list(queryWrapper);
        if (CollectionUtil.isNotEmpty(list)) {
            for (MaterialReceivingStatusChange materialReceivingStatusChange : list) {
                MaterialReceivingStatusListResp resp = MaterialReceivingStatusListResp.builder().status(materialReceivingStatusChange.getStatus())
                        .changeTime(materialReceivingStatusChange.getCreateTime())
                        .changeBy(materialReceivingStatusChange.getCreateBy()).build();
                respList.add(resp);
            }

        }
        return respList;
    }

    @Override
    @Transactional
    public void receiveMaterialReceiving(MaterialReceivingEditReq materialReceivingEditReq) {
        MaterialReceiving materialReceiving = receivingService.getById(materialReceivingEditReq.getId());
        if (ObjectUtil.isEmpty(materialReceiving)) {
            log.error("未找到领料单,id:" + materialReceivingEditReq.getId());
            throw new ServiceException("未找到领料单,id:" + materialReceivingEditReq.getId());
        }
        if (materialReceivingEditReq.getReadyTime() == null) {
            log.error("完成时间不为空");
            throw new ServiceException("完成时间不为空");
        }
        materialReceiving.setReceiveBy(SecurityUtils.getLoginUser().getUser().getNickName());
        materialReceiving.setReadyTime(materialReceivingEditReq.getReadyTime());
        materialReceiving.setStatus(1);
        receivingService.updateById(materialReceiving);
        // 状态记录(0待接收、1备料中、2待领料、3已完成、4已关闭)
        receivingStatusChangeService.save(MaterialReceivingStatusChange.builder().receivingId(materialReceiving.getId()).status(1).build());

    }

}
