package com.greenstone.mes.base.api.factory;

import com.greenstone.mes.base.api.RemoteBomService;
import com.greenstone.mes.bom.domain.BomImportRecord;
import com.greenstone.mes.bom.dto.BomImportDTO;
import com.greenstone.mes.bom.request.BomEditByPartOrderReq;
import com.greenstone.mes.bom.response.BomImportDetailListResp;
import com.greenstone.mes.bom.response.BomQueryResp;
import com.greenstone.mes.common.core.domain.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户服务降级处理
 *
 * @author ruoyi
 */
@Component
public class RemoteBomFallbackFactory implements FallbackFactory<RemoteBomService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteBomFallbackFactory.class);

    @Override
    public RemoteBomService create(Throwable throwable) {
        log.error("基础配置服务调用失败:{}", throwable.getMessage());
        return new RemoteBomService() {
            @Override
            public R<BomImportRecord> getBomImportRecord(Long id) {
                return R.fail("获取bom导入记录信息失败:" + throwable.getMessage());
            }

            @Override
            public R<List<BomImportDetailListResp>> queryBomImportDetail(Long id) {
                return R.fail("获取bom导入记录详情信息失败:" + throwable.getMessage());
            }

            @Override
            public R<BomQueryResp> getBomById(Long id) {
                return R.fail("获取bom信息失败:" + throwable.getMessage());
            }

            @Override
            public R<String> addBomListByImport(List<BomImportDTO> importDtoList) {
                return R.fail("新增bom失败:" + throwable.getMessage());
            }

            @Override
            public R<String> updateBomByPartOrder(List<BomEditByPartOrderReq> bomEditByPartOrderReqList) {
                return R.fail("修改bom失败:" + throwable.getMessage());
            }

        };
    }
}
