package com.greenstone.mes.material.application.assembler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.domain.entity.CheckRecord;
import com.greenstone.mes.material.dto.CheckRecordExportCommand;
import com.greenstone.mes.material.dto.CheckRecordListQuery;
import com.greenstone.mes.material.dto.CheckRecordSaveCommand;
import com.greenstone.mes.material.event.data.StockOperationEventData;
import com.greenstone.mes.material.interfaces.response.CheckRecordListResp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {StrUtil.class, CollUtil.class}
)
public interface CheckRecordAssembler {


    Logger log = LoggerFactory.getLogger(CheckRecordAssembler.class);

    @Mapping(target = "hasImage", expression = "java(CollUtil.isNotEmpty(command.getFiles()))")
    @Mapping(target = "imageNum", expression = "java(CollUtil.isNotEmpty(command.getFiles()) ? command.getFiles().size() : null)")
    CheckRecord toCheckRecord(CheckRecordSaveCommand command);

    default CheckRecordSaveCommand toCheckRecord(StockOperationEventData operationEventData, BaseMaterial material) {
        StockOperationEventData.StockMaterial stockMaterial = operationEventData.getMaterialList().get(0);
        StockOperationEventData.NgData ngData = operationEventData.getNgData();
        return CheckRecordSaveCommand.builder().componentCode(stockMaterial.getComponentCode())
                .projectCode(stockMaterial.getProjectCode())
                .worksheetCode(stockMaterial.getWorksheetCode())
                .materialCode(material.getCode())
                .materialVersion(material.getVersion())
                .materialName(material.getName())
                .number(stockMaterial.getNumber())
                .ngType(ngData == null ? null : ngData.getNgType())
                .subNgType(ngData == null ? null : ngData.getSubNgType())
                .remark(operationEventData.getRemark())
                .time(LocalDateTime.now())
                .sponsor(operationEventData.getSponsor()).files(ngData == null ? null : ngData.getFiles()).build();
    }

    CheckRecordListResp toCheckRecordListResp(CheckRecord checkRecord);

    List<CheckRecordListResp> toCheckRecordListRespList(List<CheckRecord> checkRecords);

    CheckRecordListQuery toListQuery(CheckRecordExportCommand exportCommand);

}
