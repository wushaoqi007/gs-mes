package com.greenstone.mes.bom.manager.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.base.api.RemoteMaterialService;
import com.greenstone.mes.material.constant.MaterialConst;
import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.bom.constant.BomConst;
import com.greenstone.mes.bom.domain.*;
import com.greenstone.mes.bom.dto.BomImportDTO;
import com.greenstone.mes.bom.dto.BomQrCodeAddDto;
import com.greenstone.mes.bom.dto.MaterialNumberDto;
import com.greenstone.mes.bom.exception.BomException;
import com.greenstone.mes.bom.manager.BomManager;
import com.greenstone.mes.bom.request.BomCompareExportReq;
import com.greenstone.mes.bom.request.BomEditByPartOrderReq;
import com.greenstone.mes.bom.request.BomExportReq;
import com.greenstone.mes.bom.request.BomIntegrityReq;
import com.greenstone.mes.bom.response.BomExportResp;
import com.greenstone.mes.bom.response.BomIntegrityResp;
import com.greenstone.mes.bom.response.BomPartExportResp;
import com.greenstone.mes.bom.response.BomQueryResp;
import com.greenstone.mes.bom.service.*;
import com.greenstone.mes.bom.wrapper.RemoteServiceWrapper;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.material.request.PartOrderAddReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BomManagerImpl implements BomManager {

    @Autowired
    private RemoteMaterialService remoteMaterialService;

    @Autowired
    private IBomImportRecordService bomImportRecordService;

    @Autowired
    private BomImportDetailService bomImportDetailService;

    @Autowired
    private IDrawingComparisonService comparisonService;

    @Autowired
    private IComparisonDetailService comparisonDetailService;

    private final DeviceService deviceService;

    private final BomService bomService;

    private final BomDetailService bomDetailService;

    private final RemoteServiceWrapper remoteServiceWrapper;

    @Autowired
    public BomManagerImpl(BomService bomService, BomDetailService bomDetailService, RemoteServiceWrapper remoteServiceWrapper,
                          DeviceService deviceService) {
        this.bomService = bomService;
        this.bomDetailService = bomDetailService;
        this.remoteServiceWrapper = remoteServiceWrapper;
        this.deviceService = deviceService;
    }


    @Override
    public BomIntegrityResp integrity(BomIntegrityReq integrityReq) {
        if (StrUtil.isBlank(integrityReq.getProjectCode()) && StrUtil.isBlank(integrityReq.getComponentName())) {
            throw new ServiceException("必须填写项目代码或组件名称");
        }

        // 默认套数为1
        if (Objects.isNull(integrityReq.getSuiteNumber())) {
            integrityReq.setSuiteNumber(1L);
        }

        // 根据查询条件拿到BOM
        Bom bomSelectCond = Bom.builder().projectCode(integrityReq.getProjectCode()).build();
        QueryWrapper<Bom> queryWrapper = new QueryWrapper<>(bomSelectCond);
        if (StrUtil.isNotBlank(integrityReq.getComponentName())) {
            queryWrapper.like("name", integrityReq.getComponentName());
        }
        List<Bom> bomList = bomService.list(queryWrapper);
        if (CollectionUtil.isEmpty(bomList)) {
            throw new ServiceException("不存在满足条件的BOM");
        }

        // 项目的齐套信息
        BomIntegrityResp integrityResp = BomIntegrityResp.builder().projectCode(bomList.get(0).getProjectCode()).
                totalMaterialNumber(0L).
                lackMaterialNumber(0L).build();
        // 组件列表
        List<BomIntegrityResp.ComponentIntegrityInfo> componentIntegrityInfoList = new ArrayList<>();
        integrityResp.setComponentList(componentIntegrityInfoList);

        for (Bom bom : bomList) {
            // 组件的齐套信息
            BomIntegrityResp.ComponentIntegrityInfo componentIntegrity = BomIntegrityResp.ComponentIntegrityInfo.builder().
                    componentName(bom.getName()).
                    componentCode(bom.getCode()).
                    componentVersion(bom.getVersion()).
                    totalMaterialNumber(0L).
                    lackMaterialNumber(0L).build();
            integrityResp.getComponentList().add(componentIntegrity);


            List<BomDetail> bomDetailList = bomDetailService.list(Wrappers.query(BomDetail.builder().bomId(bom.getId()).build()));
            if (CollectionUtil.isNotEmpty(bomDetailList)) {
                // 零件列表
                List<BomIntegrityResp.MaterialIntegrityInfo> materialIntegrityList = new ArrayList<>();
                componentIntegrity.setMaterialList(materialIntegrityList);

                for (BomDetail bomDetail : bomDetailList) {
                    R<BaseMaterial> r = remoteMaterialService.getMaterial(bomDetail.getMaterialId());
                    if (!r.isSuccess()) {
                        throw new ServiceException(r.getMsg());
                    } else {
                        BaseMaterial material = r.getData();
                        // 零件的齐套信息
                        BomIntegrityResp.MaterialIntegrityInfo materialIntegrity = BomIntegrityResp.MaterialIntegrityInfo.builder().
                                materialCode(material.getCode()).
                                materialName(material.getName()).
                                materialVersion(material.getVersion()).
                                totalNumber(bomDetail.getMaterialNumber() * integrityReq.getSuiteNumber()).build();
                        materialIntegrityList.add(materialIntegrity);

                        List<MaterialNumberDto> materialNumberDtoList = bomService.listNumberFromLpWh(material.getId());


                        if (CollectionUtil.isNotEmpty(materialNumberDtoList)) {
                            // 物料仓库列表
                            List<BomIntegrityResp.MaterialNumber> materialNumberList = new ArrayList<>();
                            materialIntegrity.setMaterialNumberList(materialNumberList);

                            long existNumber = 0;
                            for (MaterialNumberDto materialNumberDto : materialNumberDtoList) {
                                // 物料数量和所在仓库
                                BomIntegrityResp.MaterialNumber materialNumber = BomIntegrityResp.MaterialNumber.builder().whCode(materialNumberDto.getWhCode()).
                                        whName(materialNumberDto.getWhName()).
                                        number(materialNumberDto.getNumber()).build();
                                materialNumberList.add(materialNumber);

                                existNumber = existNumber + materialNumber.getNumber();

                            }
                            materialIntegrity.setExistNumber(existNumber);
                        }

                        // 零件缺少的数量，若现有量 >= 需求量，则为0
                        long lackNumber = materialIntegrity.getTotalNumber() - materialIntegrity.getExistNumber();
                        materialIntegrity.setLackNumber(lackNumber > 0 ? lackNumber : 0);
                        materialIntegrity.setLack(materialIntegrity.getLackNumber() > 0);

                        componentIntegrity.setTotalMaterialNumber(componentIntegrity.getTotalMaterialNumber() + materialIntegrity.getTotalNumber());
                        componentIntegrity.setLackMaterialNumber(componentIntegrity.getLackMaterialNumber() + materialIntegrity.getLackNumber());

                        integrityResp.setTotalMaterialNumber(integrityResp.getTotalMaterialNumber() + materialIntegrity.getTotalNumber());
                        integrityResp.setLackMaterialNumber(integrityResp.getLackMaterialNumber() + materialIntegrity.getLackNumber());
                    }

                }
            }
            // 组件齐套百分比
            if (Objects.isNull(componentIntegrity.getTotalMaterialNumber()) || componentIntegrity.getTotalMaterialNumber() <= 0) {
                componentIntegrity.setComponentIntegrityRate(0);
            } else {
                double componentIntegrityRate = (componentIntegrity.getTotalMaterialNumber() - componentIntegrity.getLackMaterialNumber()) / Double.valueOf(componentIntegrity.getTotalMaterialNumber()) * 100;
                componentIntegrity.setComponentIntegrityRate(NumberUtil.round(componentIntegrityRate, 2, RoundingMode.DOWN).doubleValue());
            }

        }
        // 项目齐套百分比
        if (Objects.isNull(integrityResp.getTotalMaterialNumber()) || integrityResp.getTotalMaterialNumber() <= 0) {
            integrityResp.setProjectIntegrityRate(0);
        } else {
            double projectIntegrityRate = (integrityResp.getTotalMaterialNumber() - integrityResp.getLackMaterialNumber()) / Double.valueOf(integrityResp.getTotalMaterialNumber()) * 100;
            integrityResp.setProjectIntegrityRate(NumberUtil.round(projectIntegrityRate, 2, RoundingMode.DOWN).doubleValue());
        }

        return integrityResp;
    }

    @Override
    public BomQueryResp getBom(Long bomId) {
        Bom bom = bomService.getById(bomId);

        List<BomQueryResp.Composition> compositions = new ArrayList<>();

        QueryWrapper<BomDetail> queryWrapper = Wrappers.query(BomDetail.builder().bomId(bomId).build());
        List<BomDetail> bomDetailList = bomDetailService.list(queryWrapper);

        for (BomDetail bomDetail : bomDetailList) {
            R<BaseMaterial> compositionResult = remoteMaterialService.getMaterial(bomDetail.getMaterialId());
            BaseMaterial composition = compositionResult.getData();
            if (Objects.isNull(composition)) {
                throw new BomException("BOM中包含了不存在的物料，请联系管理员！");
            }
            BomQueryResp.Composition compositionMaterial = BomQueryResp.Composition.builder().id(bomDetail.getId()).
                    materialId(bomDetail.getMaterialId()).
                    materialCode(composition.getCode()).
                    materialName(composition.getName()).
                    materialType(composition.getType()).
                    materialVersion(composition.getVersion()).
                    number(bomDetail.getMaterialNumber()).
                    unit(composition.getUnit()).
                    rawMaterial(composition.getRawMaterial()).
                    surfaceTreatment(composition.getSurfaceTreatment()).
                    weight(composition.getWeight()).build();
            compositions.add(compositionMaterial);
        }

        return BomQueryResp.builder().bomId(bomId).bomCode(bom.getCode()).
                bomName(bom.getName()).
                bomVersion(bom.getVersion()).
                projectCode(bom.getProjectCode()).
                compositions(compositions).build();
    }

    @Override
    public List<BomExportResp> exportBomPart(BomExportReq exportReq) {
        // 获取组件对应的物料信息
        R<BaseMaterial> componentR = remoteMaterialService.getMaterial(exportReq.getComponentId());
        if (!componentR.isPresent()) {
            throw new ServiceException("系统中不存在此组件");
        }
        BaseMaterial component = componentR.getData();
        // 获取组件对应的BOM
        QueryWrapper<Bom> bomQueryWrapper = Wrappers.query(Bom.builder().materialId(component.getId()).version(component.getVersion()).build());
        Bom bom = bomService.getOneOnly(bomQueryWrapper);
        if (Objects.isNull(bom)) {
            log.error("不存在此组件对应的BOM");
            throw new ServiceException("不存在此组件对应的BOM");
        }
        // 获取组件对应的BOM详情
        QueryWrapper<BomDetail> bomDetailQueryWrapper = Wrappers.query(BomDetail.builder().bomId(bom.getId()).build());
        List<BomDetail> bomDetails = bomDetailService.list(bomDetailQueryWrapper);

        log.info("export bom detail with material code: {}", component.getCode());
        List<Long> materialIds = bomDetails.stream().map(BomDetail::getMaterialId).collect(Collectors.toList());

        // 结果集
        List<BomExportResp> respList = new ArrayList<>();
        // 拼装结果
        for (Long materialId : materialIds) {
            if (Objects.isNull(materialId)) {
                log.error("materialId can not be null");
                continue;
            }
            R<BaseMaterial> materialR = remoteMaterialService.getMaterial(materialId);
            if (!materialR.isPresent()) {
                log.error("can find material by id: {}", materialId);
                throw new ServiceException("错误：包含有系统中不存在的零件");
            }
            BaseMaterial material = materialR.getData();

            Optional<BomDetail> bomDetail = bomDetails.stream().filter(d -> Objects.equals(d.getMaterialId(), materialId)).findFirst();

            BomExportResp exportResp = BomExportResp.builder().
                    componentCode(bomDetail.isPresent() ? component.getCode() : null).
                    componentName(bomDetail.isPresent() ? component.getName() : null).
                    componentVersion(bomDetail.isPresent() ? component.getVersion() : null).
                    materialCode(material.getCode()).
                    materialName(material.getName()).
                    materialVersion(material.getVersion()).
                    projectCode(bom.getProjectCode()).
                    surfaceTreatment(material.getSurfaceTreatment()).
                    rawMaterial(material.getRawMaterial()).
                    designer(material.getDesigner()).
                    number(bomDetail.map(BomDetail::getMaterialNumber).orElse(null)).build();
            respList.add(exportResp);
        }

        return respList;
    }

    @Override
    public List<BomPartExportResp> exportCompareItems(BomCompareExportReq exportReq) {
        // 获取组件对应的物料信息
        R<BaseMaterial> componentR = remoteMaterialService.getMaterial(exportReq.getComponentId());
        if (!componentR.isPresent()) {
            throw new ServiceException("系统中不存在此组件");
        }
        BaseMaterial component = componentR.getData();
        // 获取组件对应的BOM
        QueryWrapper<Bom> bomQueryWrapper = Wrappers.query(Bom.builder().materialId(component.getId()).version(component.getVersion()).build());
        Bom bom = bomService.getOneOnly(bomQueryWrapper);
        if (Objects.isNull(bom)) {
            log.error("不存在此组件对应的BOM");
            throw new ServiceException("不存在此组件对应的BOM");
        }
        // 获取组件对应的BOM详情
        QueryWrapper<BomDetail> bomDetailQueryWrapper = Wrappers.query(BomDetail.builder().bomId(bom.getId()).build());
        List<BomDetail> bomDetails = bomDetailService.list(bomDetailQueryWrapper);

        // 结果集
        List<BomPartExportResp> respList = new ArrayList<>();
        // 拼装结果
        for (BomCompareExportReq.PurchaseMaterial purchaseMaterial : exportReq.getMaterials()) {
            if (Objects.isNull(purchaseMaterial) || Objects.isNull(purchaseMaterial.getMaterialId())) {
                log.error("materialId can not be null");
                continue;
            }
            R<BaseMaterial> materialR = remoteMaterialService.getMaterial(purchaseMaterial.getMaterialId());
            if (!materialR.isPresent()) {
                log.error("can find material by id: {}", purchaseMaterial.getMaterialId());
                throw new ServiceException("错误：包含有系统中不存在的零件");
            }
            BaseMaterial material = materialR.getData();

            Optional<BomDetail> bomDetail = bomDetails.stream().filter(d -> Objects.equals(d.getMaterialId(), purchaseMaterial.getMaterialId())).findFirst();

            BomPartExportResp exportResp = BomPartExportResp.builder().provider(purchaseMaterial.getProvider()).
                    componentCode(bomDetail.isPresent() ? component.getCode() : null).
                    componentName(bomDetail.isPresent() ? component.getName() : null).
                    componentVersion(bomDetail.isPresent() ? component.getVersion() : null).
                    materialCode(material.getCode()).
                    materialName(material.getName()).
                    materialVersion(material.getVersion()).
                    projectCode(bom.getProjectCode()).
                    surfaceTreatment(material.getSurfaceTreatment()).
                    rawMaterial(material.getRawMaterial()).
                    designer(material.getDesigner()).
                    number(bomDetail.map(BomDetail::getMaterialNumber).orElse(null)).
                    deliveryTime(purchaseMaterial.getDeliveryTime()).build();
            respList.add(exportResp);
        }

        return respList;
    }

    @Override
    @Transactional
    public void addBom(List<BomImportDTO> bomImportDTOList) {
        // 根据项目代码分组
        Map<String, List<BomImportDTO>> projectCodeMap = bomImportDTOList.stream().collect(Collectors.groupingBy(BomImportDTO::getProjectCode));
        projectCodeMap.forEach((projectCode, projectDataList) -> {
            // 根据组件编码分组
            Map<String, List<BomImportDTO>> compCodeMap = projectDataList.stream().collect(Collectors.groupingBy(BomImportDTO::getComponentCode));
            compCodeMap.forEach((compCode, compDataList) -> {
                Bom bomSelectEntity = Bom.builder().code(compCode).build();
                Bom existBom = bomService.getOneOnly(bomSelectEntity);
                if (existBom == null) {
                    Bom bomAddEntity = Bom.builder().code(compCode).
                            version("V0").
                            name(compDataList.get(0).getComponentName()).
                            projectCode(projectCode).
                            publishStatus(BomConst.PublishStatus.PUBLISHED).build();
                    log.info("Save bom: {}", bomAddEntity);
                    bomService.save(bomAddEntity);
                    existBom = bomService.getOneOnly(bomSelectEntity);
                }

                for (BomImportDTO bomImportDto : compDataList) {
                    BaseMaterial material = BaseMaterial.builder().code(bomImportDto.getPartCode()).
                            version(bomImportDto.getPartVersion()).
                            name(bomImportDto.getPartName()).
                            rawMaterial(bomImportDto.getRawMaterial()).
                            surfaceTreatment(bomImportDto.getSurfaceTreatment()).
                            weight(bomImportDto.getWeight()).unit("pcs").
                            type(MaterialConst.Type.RAW_MATERIAL).
                            designer(bomImportDto.getDesigner()).build();
                    BaseMaterial existMaterial = remoteServiceWrapper.getWithSaveIfNotExist(material);

                    BomDetail detailSelectEntity = BomDetail.builder().bomId(existBom.getId()).materialId(existMaterial.getId()).build();
                    BomDetail existDetail = bomDetailService.getOneOnly(detailSelectEntity);
                    if (existDetail == null) {
                        BomDetail detailSaveEntity = BomDetail.builder().bomId(existBom.getId()).
                                materialId(existMaterial.getId()).
                                materialNumber(bomImportDto.getPartNumber()).build();
                        bomDetailService.save(detailSaveEntity);
                    } else {
                        BomDetail detailUpdateEntity = null;
                        detailUpdateEntity = BomDetail.builder().id(existDetail.getId()).
                                materialNumber(existDetail.getMaterialNumber() + bomImportDto.getPartNumber()).build();
                        // TODO 需要区分新增或减少
                        bomDetailService.updateById(detailUpdateEntity);
                    }
                }
                // 插入bom导入记录
                importBomRecord(existBom, compDataList);
            });
        });


    }

    @Override
    @Transactional
    public void importBomRecord(Bom bom, List<BomImportDTO> bomImportDTOList) {
        log.info("Start add importBomRecord, size: {}", bomImportDTOList.size());
        BomImportRecord bomImportRecord = BomImportRecord.builder().
                fileName("").
                projectCode(bom.getProjectCode()).
                count(bomImportDTOList.size()).
                designer(bomImportDTOList.get(0).getDesigner()).build();

        List<BomImportDetail> bomImportDetails = new ArrayList<>();
        for (BomImportDTO bomImportDto : bomImportDTOList) {
            BomImportDetail bomImportDetail = BomImportDetail.builder().projectCode(bomImportDto.getProjectCode()).
                    partOrderCode(bomImportDto.getProcessOrderCode()).
                    componentCode(bomImportDto.getComponentCode()).
                    componentName(bomImportDto.getComponentName()).
                    code(bomImportDto.getPartCode()).
                    version(bomImportDto.getPartVersion()).
                    name(bomImportDto.getPartName()).
                    buyLimit(bomImportDto.getOperation()).
                    materialNumber(bomImportDto.getPartNumber()).
                    paperNumber(bomImportDto.getPaperNumber()).
                    surfaceTreatment(bomImportDto.getSurfaceTreatment()).
                    rawMaterial(bomImportDto.getRawMaterial()).
                    weight(bomImportDto.getWeight()).
                    designer(bomImportDto.getDesigner()).
                    printTime(bomImportDto.getPrintData()).
                    buyLimit(bomImportDto.getOperation()).
                    remark(bomImportDto.getRemark()).
                    build();

            bomImportDetails.add(bomImportDetail);
        }
        addBomImportRecord(bomImportRecord, bomImportDetails);

    }

    @Override
    @Transactional
    public void addBomImportRecord(BomImportRecord bomImportRecord, List<BomImportDetail> bomImportDetailList) {
        log.info("start save bomImportRecord: {}", bomImportRecord);
        bomImportRecordService.save(bomImportRecord);
        for (BomImportDetail bomImportDetail : bomImportDetailList) {
            bomImportDetail.setRecordId(bomImportRecord.getId());
        }
        bomImportDetailService.saveBatch(bomImportDetailList);

    }

    @Override
    @Transactional
    public void addComparisonResultAndDetail(DrawingComparison drawingComparison, List<ComparisonDetail> comparisonDetails) {
        log.info("start save addComparisonResultAndDetail: {}", drawingComparison);
        comparisonService.save(drawingComparison);
        for (ComparisonDetail comparisonDetail : comparisonDetails) {
            comparisonDetail.setComparisonId(drawingComparison.getId());
        }
        comparisonDetailService.saveBatch(comparisonDetails);

    }


    @Override
    @Transactional
    public void addBom(Bom bom, List<BomDetail> bomDetailList, boolean updateSupport) {
        log.info("start save bom: {}", bom);
        // 支持更新：先删除BOM和详情后添加
        if (updateSupport) {
            Bom bomSelect = Bom.builder().code(bom.getCode()).version(bom.getVersion()).build();
            Bom bomFromDb = bomService.getOneOnly(bomSelect);
            if (Objects.nonNull(bomFromDb)) {
                log.info("bom force update, delete before add");
                delete(bomFromDb.getId(), true);
                QueryWrapper<BomDetail> queryWrapper = Wrappers.query(BomDetail.builder().bomId(bomFromDb.getId()).build());
                bomDetailService.remove(queryWrapper);
            }

        }
        Bom checkEntity = Bom.builder().code(bom.getCode()).version(bom.getVersion()).build();
        bomService.duplicatedCheck(checkEntity, "existing.bom.with.same.code.and.version");
        if (Objects.isNull(bom.getPublishStatus())) {
            bom.setPublishStatus(BomConst.PublishStatus.UNPUBLISHED);
        }
        bomService.save(bom);
        for (BomDetail bomDetail : bomDetailList) {
            bomDetail.setBomId(bom.getId());
        }
        bomDetailService.saveBatch(bomDetailList);

    }

    @Override
    public void publish(Bom bom) {
        bom.setPublishStatus(BomConst.PublishStatus.PUBLISHED);
        bomService.updateById(bom);
    }

    @Override
    @Transactional
    public void delete(Long bomId, boolean force) {
        // 删除组件信息
        QueryWrapper<Device> deviceQueryWrapper = Wrappers.query(Device.builder().bomId(bomId).build());
        deviceService.remove(deviceQueryWrapper);
        // 删除BOM
        bomService.removeById(bomId);
        QueryWrapper<BomDetail> bomDetailRemove = Wrappers.query(BomDetail.builder().bomId(bomId).build());
        bomDetailService.remove(bomDetailRemove);
    }

    @Override
    @Transactional
    public void update(Bom bom, List<BomDetail> bomDetailList) {
        Bom bomFromDB = bomService.getById(bom.getId());
        if (Objects.nonNull(bomFromDB.getPublishStatus()) && bomFromDB.getPublishStatus() != BomConst.PublishStatus.UNPUBLISHED) {
            throw new BomException("无法修改已发布的BOM！");
        }

        // 如果BOM没有发布状态，则需要修复缺失的状态数据
        if (Objects.isNull(bomFromDB.getPublishStatus())) {
            bom.setPublishStatus(BomConst.PublishStatus.UNPUBLISHED);
        }

        bomService.updateById(bom);

        QueryWrapper<BomDetail> queryWrapper = Wrappers.query(BomDetail.builder().bomId(bom.getId()).build());
        bomDetailService.remove(queryWrapper);

        for (BomDetail bomDetail : bomDetailList) {
            bomDetail.setBomId(bom.getId());
        }
        bomDetailService.saveBatch(bomDetailList);
    }

    @Override
    @Transactional
    public void addFromQrCode(BomQrCodeAddDto bomQrCodeAddDto) {
        // 添加到机加工单
        PartOrderAddReq.OrderDetail orderDetail = PartOrderAddReq.OrderDetail.builder().
                componentCode(bomQrCodeAddDto.getComponentCode()).
                componentName(bomQrCodeAddDto.getComponentName()).
                partCode(bomQrCodeAddDto.getPartCode()).
                partName(bomQrCodeAddDto.getPartName()).
                partVersion(bomQrCodeAddDto.getPartVersion()).
                partNumber(bomQrCodeAddDto.getPartNumber()).
                paperNumber(bomQrCodeAddDto.getPaperNumber()).
                rawMaterial(bomQrCodeAddDto.getRawMaterial()).
                surfaceTreatment(bomQrCodeAddDto.getSurfaceTreatment()).
                weight(bomQrCodeAddDto.getWeight()).designer(bomQrCodeAddDto.getDesigner()).
                purchaseReason(bomQrCodeAddDto.getPurchaseReason()).companyType(bomQrCodeAddDto.getCompanyType()).build();
        List<PartOrderAddReq.OrderDetail> orderDetailList = new ArrayList<>();
        orderDetailList.add(orderDetail);
        PartOrderAddReq partOrderAddReq = PartOrderAddReq.builder().orderCode(bomQrCodeAddDto.getPartOrderCode()).
                projectCode(bomQrCodeAddDto.getProjectCode()).
                orderDetailList(orderDetailList).build();
        remoteServiceWrapper.addPartOrder(partOrderAddReq);
    }

    @Override
    public void updateBomByPartOrder(List<BomEditByPartOrderReq> bomEditByPartOrderReqList) {
        for (BomEditByPartOrderReq bomEditByPartOrderReq : bomEditByPartOrderReqList) {
            QueryWrapper<Bom> bomQueryWrapper = Wrappers.query(Bom.builder().code(bomEditByPartOrderReq.getComponentCode()).build());
            Bom bom = bomService.getOneOnly(bomQueryWrapper);
            if (Objects.isNull(bom)) {
                throw new BomException("未找到BOM，组件号为：" + bomEditByPartOrderReq.getComponentCode());
            }
            R<BaseMaterial> baseMaterialR = remoteMaterialService.queryMaterial(bomEditByPartOrderReq.getMaterialCode(), bomEditByPartOrderReq.getMaterialVersion());
            if (baseMaterialR.isNotPresent()) {
                throw new BomException("未找到物料，code/version：" + bomEditByPartOrderReq.getMaterialCode() + "/" + bomEditByPartOrderReq.getMaterialVersion());
            }
            QueryWrapper<BomDetail> bomDetailQueryWrapper = Wrappers.query(BomDetail.builder().bomId(bom.getId()).materialId(baseMaterialR.getData().getId()).build());
            BomDetail bomDetail = bomDetailService.getOneOnly(bomDetailQueryWrapper);
            if (Objects.isNull(bomDetail)) {
                throw new BomException("未找到BOM详情，bomId为：" + bom.getId() + ",materialId:" + baseMaterialR.getData().getId());
            }
            // 更新
            if (bomEditByPartOrderReq.getMaterialNumber() != null && bomEditByPartOrderReq.getMaterialNumber().intValue() > 0) {
                bomDetail.setMaterialNumber(bomEditByPartOrderReq.getMaterialNumber());
                bomDetailService.updateById(bomDetail);
            }
            // 删除
            if (bomEditByPartOrderReq.getMaterialNumber() != null && bomEditByPartOrderReq.getMaterialNumber().intValue() == 0) {
                bomDetailService.removeById(bomDetail);
            }

        }

    }


}
