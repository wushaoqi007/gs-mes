
package com.greenstone.mes.material.application.assembler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.material.domain.entity.PartReceive;
import com.greenstone.mes.material.domain.entity.PartReceiveRecord;
import com.greenstone.mes.material.dto.PartReceiveEditCommand;
import com.greenstone.mes.material.dto.PartReceiveSaveCommand;
import com.greenstone.mes.material.event.data.StockOperationEventData;
import com.greenstone.mes.material.interfaces.response.PartReceiveR;
import com.greenstone.mes.material.interfaces.response.PartReceiveRecordR;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {StrUtil.class, CollUtil.class}
)
public interface PartReceiveAssembler {


    Logger log = LoggerFactory.getLogger(PartReceiveAssembler.class);

    PartReceive toPartReceive(PartReceiveSaveCommand.PartInfo partInfo);

    List<PartReceive> toPartReceives(List<PartReceiveSaveCommand.PartInfo> partInfoList);

    PartReceive toPartReceive(PartReceiveEditCommand.PartInfo partInfo);

    default List<PartReceive> toPartReceives(PartReceiveEditCommand editCmd) {
        List<PartReceive> partReceiveList = new ArrayList<>();
        for (PartReceiveEditCommand.PartInfo partInfo : editCmd.getPartInfoList()) {
            PartReceive partReceive = toPartReceive(partInfo);
            partReceive.setRecordId(editCmd.getRecordId());
            partReceiveList.add(partReceive);
        }
        return partReceiveList;
    }

    PartReceiveRecordR toPartReceiveRecordR(PartReceiveRecord partReceiveRecord);

    List<PartReceiveRecordR> toPartReceiveRecordListR(List<PartReceiveRecord> partReceiveRecordList);

    @Mapping(target = "partsGroupId", source = "recordId")
    PartReceiveR toPartReceiveR(PartReceive partReceive);

    List<PartReceiveR> toPartReceiveRs(List<PartReceive> partReceiveList);

    default PartReceiveSaveCommand toPartReceiveSaveCommand(List<StockOperationEventData.StockMaterial> stockMaterialList) {
        List<PartReceiveSaveCommand.PartInfo> partInfoList = new ArrayList<>();
        PartReceiveSaveCommand partReceiveSaveCommand = PartReceiveSaveCommand.builder().partInfoList(partInfoList).build();
        for (StockOperationEventData.StockMaterial stockMaterial : stockMaterialList) {
            PartReceiveSaveCommand.PartInfo partInfo = PartReceiveSaveCommand.PartInfo.builder()
                    .componentCode(stockMaterial.getComponentCode())
                    .projectCode(stockMaterial.getProjectCode())
                    .worksheetCode(stockMaterial.getWorksheetCode())
                    .partCode(stockMaterial.getMaterial().getCode())
                    .partVersion(stockMaterial.getMaterial().getVersion())
                    .partName(stockMaterial.getMaterial().getName())
                    .number(stockMaterial.getNumber())
                    .materialId(stockMaterial.getMaterial().getId()).build();
            partInfoList.add(partInfo);
        }
        return partReceiveSaveCommand;
    }

    default PartReceiveEditCommand toPartReceiveEditCmd(List<StockOperationEventData.StockMaterial> stockMaterialList, StockOperationEventData operationEventData) {
        List<PartReceiveEditCommand.PartInfo> partInfoList = new ArrayList<>();
        PartReceiveEditCommand partReceiveEditCommand = PartReceiveEditCommand.builder().recordId(operationEventData.getPartsGroupId()).partInfoList(partInfoList).build();
        for (StockOperationEventData.StockMaterial stockMaterial : stockMaterialList) {
            PartReceiveEditCommand.PartInfo partInfo = PartReceiveEditCommand.PartInfo.builder().warehouseId(operationEventData.getWarehouse().getId())
                    .componentCode(stockMaterial.getComponentCode())
                    .projectCode(stockMaterial.getProjectCode())
                    .worksheetCode(stockMaterial.getWorksheetCode())
                    .partCode(stockMaterial.getMaterial().getCode())
                    .partVersion(stockMaterial.getMaterial().getVersion())
                    .partName(stockMaterial.getMaterial().getName())
                    .number(stockMaterial.getNumber())
                    .materialId(stockMaterial.getMaterial().getId()).build();
            partInfoList.add(partInfo);
        }
        return partReceiveEditCommand;
    }

}
