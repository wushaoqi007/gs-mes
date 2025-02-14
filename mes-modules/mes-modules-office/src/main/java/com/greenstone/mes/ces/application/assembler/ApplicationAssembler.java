package com.greenstone.mes.ces.application.assembler;

import com.greenstone.mes.ces.application.dto.cmd.CesApplicationAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.CesApplicationEditCmd;
import com.greenstone.mes.ces.application.dto.event.OrderAddE;
import com.greenstone.mes.ces.application.dto.event.ReceiptAddE;
import com.greenstone.mes.ces.application.dto.result.CesApplicationResult;
import com.greenstone.mes.ces.application.dto.result.CesApplicationWaitHandleResult;
import com.greenstone.mes.ces.domain.entity.CesApplication;
import com.greenstone.mes.ces.domain.entity.CesApplicationItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/1/31 16:10
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ApplicationAssembler {

    // CesApplicationAddCmd
    CesApplication toCesApplication(CesApplicationAddCmd addCmd);

    CesApplicationItem fromAddCmdItem(CesApplicationAddCmd.Item item);

    List<CesApplicationItem> fromAddCmdItems(List<CesApplicationAddCmd.Item> items);

    // CesApplicationEditCmd
    CesApplication toCesApplication(CesApplicationEditCmd editCmd);

    CesApplicationItem fromEditCmdItem(CesApplicationEditCmd.Item item);

    List<CesApplicationItem> fromEditCmdItems(List<CesApplicationEditCmd.Item> items);

    // CesApplicationListR
    List<CesApplicationResult> toCesApplicationListRs(List<CesApplication> applications);

    CesApplicationResult toCesApplicationListR(CesApplication application);

    CesApplicationResult.Item toCesApplicationListRItem(CesApplicationItem item);

    List<CesApplicationResult.Item> toCesApplicationListRItems(List<CesApplicationItem> items);


    // Event
    @Mapping(target = "items", source = "orderAddEs")
    CesApplication toCesApplicationFromOrderE(OrderAddE event, List<OrderAddE.Item> orderAddEs);

    @Mapping(target = "id", source = "applicationItemId")
    CesApplicationItem toCesApplicationItemFromOrderE(OrderAddE.Item orderItem);

    @Mapping(target = "items", source = "receiptAddEs")
    CesApplication toCesApplicationFromReceiptE(ReceiptAddE event, List<ReceiptAddE.Item> receiptAddEs);

    @Mapping(target = "id", source = "applicationItemId")
    CesApplicationItem toCesApplicationItemFromReceiptE(ReceiptAddE.Item receiptItem);

    CesApplicationWaitHandleResult toCesApplicationWaitHandleListR(CesApplication applications);

    default List<CesApplicationWaitHandleResult> toCesApplicationWaitHandleListRs(List<CesApplication> applications) {
        List<CesApplicationWaitHandleResult> waitHandleResults = new ArrayList<>();
        for (CesApplication application : applications) {
            CesApplicationWaitHandleResult cesApplicationWaitHandleResult = toCesApplicationWaitHandleListR(application);
            cesApplicationWaitHandleResult.setBillType("consumable");
            waitHandleResults.add(cesApplicationWaitHandleResult);
        }
        return waitHandleResults;
    }
}
