package com.greenstone.mes.material.interfaces.transfer;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.utils.file.Base64ToMultipartFile;
import com.greenstone.mes.material.application.dto.StockTransferVo;
import com.greenstone.mes.material.infrastructure.enums.BillOperation;
import com.greenstone.mes.material.interfaces.request.StockMobileTransferNgReq;
import com.greenstone.mes.material.interfaces.request.StockTransferNgReq;
import com.greenstone.mes.material.interfaces.request.StockTransferReq;
import org.mapstruct.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/11/18 13:35
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Date.class, List.class, StrUtil.class, CollUtil.class}
)
public interface StockTransfer {

    @Mapping(target = "materialInfoList", source = "req")
    @Mapping(target = "ngData", source = "req", qualifiedByName = "toNgData")
    StockTransferVo transfer(StockTransferNgReq req);

    @Named("toNgData")
    default StockTransferVo.NgData toNgData(StockTransferNgReq req) {
        return StockTransferVo.NgData.builder().files(req.getFiles()).ngType(req.getNgType()).subNgType(req.getSubNgType()).build();
    }

    default List<StockTransferVo.MaterialInfo> toMaterials(StockTransferNgReq req) {
        return List.of(transferMaterial(req));
    }

    @Mapping(target = "componentCode", source = "req", qualifiedByName = "toSingleComponentCode")
    StockTransferVo.MaterialInfo transferMaterial(StockTransferNgReq req);

    @Mapping(target = "materialInfoList", source = "materialList")
    StockTransferVo transfer(StockTransferReq req);

    List<StockTransferVo.MaterialInfo> transferList(List<StockTransferReq.Material> materials);

    @Mapping(target = "componentCode", source = "material", qualifiedByName = "toComponentCode")
    StockTransferVo.MaterialInfo transfer(StockTransferReq.Material material);

    @Mapping(target = "materialInfoList", source = "req")
    @Mapping(target = "ngData", source = "req", qualifiedByName = "toNgData")
    StockTransferVo transfer(StockMobileTransferNgReq req);

    @Named("toNgData")
    default StockTransferVo.NgData toNgData(StockMobileTransferNgReq req) {
        List<MultipartFile> multipartFiles = null;
        List<StockMobileTransferNgReq.Image> files = req.getFiles();
        if (CollUtil.isNotEmpty(files)) {
            multipartFiles = transferBase64s(files.stream().map(StockMobileTransferNgReq.Image::getImageBase64).toList());
        }
        return StockTransferVo.NgData.builder()
                .files(multipartFiles)
                .ngType(req.getNgType()).subNgType(req.getSubNgType()).build();
    }

    default List<StockTransferVo.MaterialInfo> toMaterials(StockMobileTransferNgReq req) {
        return List.of(transferMaterial(req));
    }

    @Mapping(target = "componentCode", source = "req", qualifiedByName = "toMobileComponentCode")
    StockTransferVo.MaterialInfo transferMaterial(StockMobileTransferNgReq req);


    default BillOperation toStockOperation(int stockOpId) {
        return BillOperation.getById(stockOpId);
    }

    List<MultipartFile> transferBase64s(List<String> base64StrList);

    default MultipartFile transferBase64(String base64Str) {
        String[] base64Array = base64Str.split(",");
        String dataUir, data;
        if (base64Array.length > 1) {
            dataUir = base64Array[0];
            data = base64Array[1];
        } else {
            //默认构建为图片
            dataUir = "data:image/jpg;base64";
            data = base64Array[0];
        }
        return new Base64ToMultipartFile(data, dataUir);
    }

    @Named("toComponentCode")
    default String toComponentCode(StockTransferReq.Material material) {
        if (material.getComponentCode().length() == 2) {
            return material.getProjectCode() + "-" + material.getComponentCode();
        }
        return material.getComponentCode();
    }

    @Named("toSingleComponentCode")
    default String toSingleComponentCode(StockTransferNgReq req) {
        if (req.getComponentCode().length() == 2) {
            return req.getProjectCode() + "-" + req.getComponentCode();
        }
        return req.getComponentCode();
    }

    @Named("toMobileComponentCode")
    default String toMobileComponentCode(StockMobileTransferNgReq req) {
        if (req.getComponentCode().length() == 2) {
            return req.getProjectCode() + "-" + req.getComponentCode();
        }
        return req.getComponentCode();
    }
}
