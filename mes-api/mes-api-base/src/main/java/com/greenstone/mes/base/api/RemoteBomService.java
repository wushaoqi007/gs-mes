package com.greenstone.mes.base.api;

import com.greenstone.mes.base.api.factory.RemoteBomFallbackFactory;
import com.greenstone.mes.bom.domain.BomImportRecord;
import com.greenstone.mes.bom.dto.BomImportDTO;
import com.greenstone.mes.bom.request.BomEditByPartOrderReq;
import com.greenstone.mes.bom.response.BomImportDetailListResp;
import com.greenstone.mes.bom.response.BomQueryResp;
import com.greenstone.mes.common.core.constant.ServiceNameConstants;
import com.greenstone.mes.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 日志服务
 *
 * @author ruoyi
 */
@FeignClient(contextId = "remoteBomService", value = ServiceNameConstants.PRODUCT_SERVICE, fallbackFactory = RemoteBomFallbackFactory.class)
public interface RemoteBomService {
    /**
     * 获取导入记录
     *
     * @param id 导入记录id
     */
    @GetMapping("/bom/import/{id}")
    R<BomImportRecord> getBomImportRecord(@PathVariable("id") Long id);

    /**
     * 获取导入记录详情
     *
     * @param id 导入记录id
     */
    @GetMapping("/bom/import/detail/list/{id}")
    R<List<BomImportDetailListResp>> queryBomImportDetail(@PathVariable("id") Long id);

    /**
     * 获取bom信息
     *
     * @param id id
     */
    @GetMapping("/bom/{id}")
    R<BomQueryResp> getBomById(@PathVariable("id") Long id);

    @PostMapping("/bom/byImport")
    R<String> addBomListByImport(@RequestBody List<BomImportDTO> importDtoList);


    @PutMapping("/bom/updateByPartOrder")
    R<String> updateBomByPartOrder(@RequestBody List<BomEditByPartOrderReq> bomEditByPartOrderReqList);

}
