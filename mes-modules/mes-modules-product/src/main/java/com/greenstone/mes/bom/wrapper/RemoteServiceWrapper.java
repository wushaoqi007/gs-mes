package com.greenstone.mes.bom.wrapper;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.base.api.RemoteMaterialService;
import com.greenstone.mes.base.api.RemotePartOrderService;
import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.material.request.PartOrderAddReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author gu_renkai
 * @date 2022/8/5 13:28
 */

@Slf4j
@Service
public class RemoteServiceWrapper {

    private final RemoteMaterialService remoteMaterialService;

    private final RemotePartOrderService remotePartOrderService;

    @Autowired
    public RemoteServiceWrapper(RemoteMaterialService remoteMaterialService, RemotePartOrderService remotePartOrderService) {
        this.remoteMaterialService = remoteMaterialService;
        this.remotePartOrderService = remotePartOrderService;
    }

    /**
     * 获取物料信息，当物料不存在时，先保存再获取
     *
     * @param material 物料信息
     * @return 物料信息
     */
    public BaseMaterial getWithSaveIfNotExist(BaseMaterial material) {
        R<BaseMaterial> existMaterialR = remoteMaterialService.queryMaterial(material.getCode(), material.getVersion());
        if (existMaterialR.isNotPresent()) {
            throw new ServiceException(StrUtil.format("无法获取零件信息 {} {}", material.getCode(), material.getVersion()));
        }
        return existMaterialR.getData();
    }

    public Long addPartOrder(PartOrderAddReq partOrderAddReq) {
        R<Long> resultR = remotePartOrderService.addPartOrder(partOrderAddReq);
        if (resultR.isFail()) {
            log.error("Error add part order: {}", resultR.getMsg());
            throw new ServiceException(StrUtil.format("添加机加工单失败：{}", resultR.getMsg()));
        } else {
            return resultR.getData();
        }
    }

}
