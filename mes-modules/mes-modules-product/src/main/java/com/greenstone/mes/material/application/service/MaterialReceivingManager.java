package com.greenstone.mes.material.application.service;

import com.greenstone.mes.material.request.MaterialReceivingAddReq;
import com.greenstone.mes.material.request.MaterialReceivingEditReq;
import com.greenstone.mes.material.response.MaterialReceivingDetailResp;
import com.greenstone.mes.material.response.MaterialReceivingStatusListResp;

import java.util.List;

/**
 * 领料单接口
 *
 * @author wushaoqi
 * @date 2022-08-15-8:38
 */
public interface MaterialReceivingManager {

    /**
     * 新增领料单
     *
     * @param receivingAddReq
     * @return 领料单ID
     */
    Long insertMaterialReceiving(MaterialReceivingAddReq receivingAddReq);

    /**
     * 补全领料单详情数据
     *
     * @param list
     * @return
     */
    List<MaterialReceivingDetailResp> completeReceivingDetail(List<MaterialReceivingDetailResp> list);

    /**
     * 修改领料单状态
     */
    void updateMaterialReceivingStatus(MaterialReceivingEditReq materialReceivingEditReq);

    /**
     * 获取状态列表
     *
     * @param id 领料单ID
     */
    List<MaterialReceivingStatusListResp> getStatusListById(Long id);

    /**
     * 接收领料单
     */
    void receiveMaterialReceiving(MaterialReceivingEditReq materialReceivingEditReq);

    /**
     * 备料
     *
     * @param id 领料单ID
     * @return 备料页面信息
     */
    List<MaterialReceivingDetailResp> prepare(Long id);

}
