package com.greenstone.mes.bom.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.greenstone.mes.bom.domain.Bom;
import com.greenstone.mes.bom.domain.BomDetail;
import com.greenstone.mes.bom.dto.BomImportDTO;
import com.greenstone.mes.bom.dto.BomQrCodeAddDto;
import com.greenstone.mes.bom.manager.BomManager;
import com.greenstone.mes.bom.request.*;
import com.greenstone.mes.bom.response.BomExportResp;
import com.greenstone.mes.bom.response.BomListResp;
import com.greenstone.mes.bom.response.BomPartExportResp;
import com.greenstone.mes.bom.service.BomService;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.ValidationUtils;
import com.greenstone.mes.common.core.utils.poi.ExcelUtil;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.log.annotation.Log;
import com.greenstone.mes.common.log.enums.BusinessType;
import com.greenstone.mes.common.security.annotation.RequiresPermissions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * BOMController
 *
 * @author gu_renkai
 * @date 2022-01-25
 */
@Slf4j
@RestController
@RequestMapping("/bom")
public class BomController extends BaseController {

    private final BomManager bomManager;

    private final BomService bomService;

    @Autowired
    public BomController(BomManager bomManager, BomService bomService) {
        this.bomManager = bomManager;
        this.bomService = bomService;
    }

    /**
     * 查询BOM列表
     */
    @GetMapping("/list")
    public TableDataInfo list(Bom bom) {
        startPage();
        List<Bom> list = bomService.list(new QueryWrapper<>(bom).orderBy(true, false, "create_time"));
        TableDataInfo tableDataInfo = getDataTable(list);
        List<BomListResp> respList = new ArrayList<>();
        for (Bom bom1 : list) {
            BomListResp resp = BomListResp.builder().bomId(bom1.getId()).
                    bomCode(bom1.getCode()).
                    bomName(bom1.getName()).
                    bomVersion(bom1.getVersion()).
                    projectCode(bom1.getProjectCode()).
                    publishStatus(bom1.getPublishStatus()).build();
            respList.add(resp);
        }
        tableDataInfo.setData(respList);
        return tableDataInfo;
    }


    /**
     * bom导入
     */
    @Log(title = "BOM", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public AjaxResult importData(MultipartFile file) throws Exception {
        log.info("Receive bom import request");
        // 将表格内容转为对象
        ExcelUtil<MaterialImportReq> util = new ExcelUtil<>(MaterialImportReq.class);
        List<MaterialImportReq> materialImportReqList = util.importExcel(file.getInputStream());
        log.info("Import content: {}", materialImportReqList);
        if (CollectionUtil.isEmpty(materialImportReqList)) {
            log.error("Content in file is empty!");
            return AjaxResult.error("导入的数据不能为空");
        }
        // 将序号为空的数据排除
        List<MaterialImportReq> usefulImportData = materialImportReqList.stream().filter(d -> StrUtil.isNotBlank(d.getIndex())).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(usefulImportData)) {
            log.error("Content in file didn't have number column");
            return AjaxResult.error("导入数据的序号不能为空");
        }
        // 校验表格数据
        String validateResult = ValidationUtils.validate(usefulImportData);
        if (Objects.nonNull(validateResult)) {
            log.error(validateResult);
            throw new ServiceException(validateResult);
        }

        List<BomImportDTO> importDtoList = new ArrayList<>();
        String orderCode = null;
        for (MaterialImportReq importData : usefulImportData) {
            if (orderCode == null) {
                orderCode = importData.getPartOrderCode();
            } else if (!orderCode.equals(importData.getPartOrderCode())) {
                log.error("More than one part order code in one part order");
                throw new ServiceException("同一次导入零件的机加工单号必须一致");
            }
            // 零件信息中间使用空格分割零件号和零件名称
            String[] nameCodeVersion = importData.getPartCodeNameVersion().split(" ");
            if (nameCodeVersion.length != 2) {
                log.error("Part name is invalid: {}", importData.getPartCodeNameVersion());
                throw new ServiceException(StrUtil.format("零件名称不正确: {}", importData.getPartCodeNameVersion()));
            }
            String partCode = nameCodeVersion[0];
            // 零件名称倒数第二个字符为V或v时，表示最后两位是版本
            String partNameVersion = nameCodeVersion[1];
            if (StrUtil.isEmpty(partNameVersion)) {
                log.error("Part nameVersion is invalid: {}", partNameVersion);
                throw new ServiceException(StrUtil.format("零件名称不正确：{}", partNameVersion));
            }
            String partName = null;
            String partVersion = null;
            if (partNameVersion.length() > 2) {
                char secondLastChar = partNameVersion.charAt(partNameVersion.length() - 2);
                if (secondLastChar == 'V' || secondLastChar == 'v') {
                    partName = partNameVersion.substring(0, partNameVersion.length() - 2);
                    partVersion = partNameVersion.substring(partNameVersion.length() - 2).toUpperCase();
                }
            }
            if (partName == null) {
                partName = partNameVersion;
            }
            if (partVersion == null) {
                partVersion = "V0";
            }
            // 组件前两位是数字的话，就作为组件的编号，否则使用组件全称作为编号
            String componentCodeName = importData.getComponentName();
            String componentCode;
            String componentName;
            if (componentCodeName.length() > 2 && StrUtil.isNumeric(componentCodeName.substring(0, 2))) {
                componentCode = importData.getProjectCode() + "-" + componentCodeName.substring(0, 2);
                componentName = componentCodeName.substring(2);
            } else {
                componentCode = importData.getProjectCode() + "-" + componentCodeName;
                componentName = componentCodeName;
            }

            BomImportDTO importDto = BomImportDTO.builder().projectCode(importData.getProjectCode()).
                    processOrderCode(importData.getPartOrderCode()).
                    partCode(partCode).
                    partVersion(partVersion).
                    partName(partName).
                    componentCode(componentCode).
                    componentName(componentName).
                    partNumber(importData.getNumber()).
                    rawMaterial(importData.getRawMaterial()).
                    surfaceTreatment(importData.getSurfaceTreatment()).
                    weight(importData.getWeight()).
                    designer(importData.getDesigner()).
                    operation(importData.getOperation()).
                    remark(importData.getRemark()).build();
            // 将新规都视为新增，目前只支持新增和减少到
            if (importData.getOperation() == null || "新规".equals(importData.getOperation())) {
                importDto.setOperation("新增");
            } else {
                importDto.setOperation(importData.getOperation());
            }
            if (!"新规".equals(importDto.getOperation()) && !"新增".equals(importDto.getOperation()) && !"减少到".equals(importDto.getOperation())) {
                log.error("Error operation '{}' with part {} ", importData.getOperation(), importData.getPartCodeNameVersion());
                throw new ServiceException(StrUtil.format("零件'{}'的购买区分'{}'不支持", importData.getPartCodeNameVersion(), importData.getOperation()));
            }
            // 打印日期默认当天
            importDto.setPrintData(importData.getPrintData() == null ? new Date() : importData.getPrintData());

            importDtoList.add(importDto);
        }

        bomManager.addBom(importDtoList);
        return AjaxResult.success();

    }

    /**
     * 图纸比对导出
     */
    @Log(title = "机加工BOM导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export/part")
    public void exportPartData(HttpServletResponse response, @RequestBody @Validated BomCompareExportReq exportReq) {
        List<BomPartExportResp> exportResp = bomManager.exportCompareItems(exportReq);
        ExcelUtil<BomPartExportResp> util = new ExcelUtil<>(BomPartExportResp.class);
        util.exportExcel(response, exportResp, "加工件列表");
    }

    /**
     * bom导出
     */
    @Log(title = "BOM导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void exportData(HttpServletResponse response, @RequestBody @Validated BomExportReq exportReq) {
        List<BomExportResp> exportResp = bomManager.exportBomPart(exportReq);
        ExcelUtil<BomExportResp> util = new ExcelUtil<>(BomExportResp.class);
        util.exportExcel(response, exportResp, "BOM导出");
    }

    /**
     * 齐套检查
     */
    @GetMapping(value = "/integrity")
    public AjaxResult integrity(BomIntegrityReq integrityReq) {
        return AjaxResult.success(bomManager.integrity(integrityReq));
    }


    /**
     * 获取BOM详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(bomManager.getBom(id));
    }

    /**
     * 新增BOM
     */
    @Log(title = "BOM", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody @Validated BomAddReq addRequest) {
        Bom bom = Bom.builder().code(addRequest.getCode()).
                name(addRequest.getName()).
                materialId(addRequest.getMaterialId()).
                version(addRequest.getVersion()).
                projectCode(addRequest.getProjectCode()).build();

        List<BomDetail> bomDetailList = new ArrayList<>();
        for (BomAddReq.Composition composition : addRequest.getCompositions()) {
            BomDetail bomDetail = BomDetail.builder().materialId(composition.getMaterialId()).
                    materialNumber(composition.getNumber()).build();
            bomDetailList.add(bomDetail);
        }

        bomManager.addBom(bom, bomDetailList, false);
        return AjaxResult.success("新增成功");
    }

    /**
     * 发布BOM
     */
    @Log(title = "BOM", businessType = BusinessType.UPDATE)
    @PutMapping("/publish")
    public AjaxResult publish(@RequestBody Bom bom) {
        bomManager.publish(bom);
        return AjaxResult.success("发布成功");
    }

    /**
     * 修改BOM
     */
    @Log(title = "BOM", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated BomEditReq editRequest) {
        Bom bom = Bom.builder().id(editRequest.getId()).
                name(editRequest.getName()).
                version(editRequest.getVersion()).
                materialId(editRequest.getMaterialId()).
                projectCode(editRequest.getProjectCode()).build();

        List<BomDetail> bomDetailList = new ArrayList<>();
        for (BomEditReq.Composition composition : editRequest.getCompositions()) {
            BomDetail bomDetail = BomDetail.builder().materialId(composition.getMaterialId()).
                    materialNumber(composition.getNumber()).build();
            bomDetailList.add(bomDetail);
        }

        bomManager.update(bom, bomDetailList);
        return AjaxResult.success("修改成功");
    }

    /**
     * 删除BOM
     */
    @Log(title = "BOM", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        bomManager.delete(id, false);
        return AjaxResult.success("删除成功");
    }

    /**
     * 通过二维码上传BOM
     * 二维码内容为：V2|加工单号|项目代码|组件号和名称|零件号和名称|零件版本|零件数量|图纸数量|重量|材料|表面处理|设计
     * 组件号和名称：前面两位数字是组件编号，后面是组件名称，如：01组件
     * 零件号和名称：零件号和名称使用空格隔开，如：MINA7209 挡板
     */
    @PostMapping("addFromQrCode")
    public AjaxResult addFromQrCode(@RequestBody @Validated BomQrCodeAddReq qrCodeAddReq) {
        log.info("Request data: {}", qrCodeAddReq);
        String componentCodeName = qrCodeAddReq.getComponentCodeName();
        // 一厂扫码才校验组件前两位为数字
        if (qrCodeAddReq.getCompanyType() == 1) {
            boolean isNumeric = StrUtil.isNumeric(componentCodeName.substring(0, 2));
            if (!isNumeric) {
                log.error("Error component name '{}', must start with two digits", componentCodeName);
                throw new ServiceException(StrUtil.format("组件名称必须以两位数字开头作为此组件的编码'{}'", componentCodeName));
            }
        }
        String partCodeName = qrCodeAddReq.getPartCodeName();
        partCodeName = partCodeName.trim();
        String[] partNameSplits = partCodeName.split(" ");
        if (partNameSplits.length < 2) {
            log.error("Error part name '{}', must contain and only contain part code and name", partCodeName);
            throw new ServiceException(StrUtil.format("零件号和零件名称必须使用空格隔开：'{}'", partCodeName));
        }
        for (String partNameSplit : partNameSplits) {
            if (StrUtil.isEmpty(partNameSplit)) {
                log.error("Error part name '{}', code or name is empty", partCodeName);
                throw new ServiceException(StrUtil.format("零件号或名称不能为空，且不能有多个连续的空格'{}'", partCodeName));
            }
        }
        String partCode = partNameSplits[0];
        String partName = partCodeName.substring(partCode.length() + 1);
        String componentCode = "";
        String componentName = "";
        // 二厂和一厂组件号零件号格式不同
        if (qrCodeAddReq.getCompanyType() == 2) {
            componentCode = componentCodeName;
            componentName = componentCodeName;
            // 二厂零件号必须由2个-组成
            String[] splitPartCode = partCode.split("-");
            if (splitPartCode.length < 3) {
                log.error("Error part code '{}', code is error", partCodeName);
                throw new ServiceException(StrUtil.format("零件号必须由两个”-“，'{}'", partCodeName));
            }
        } else {
            componentCode = qrCodeAddReq.getProjectCode() + "-" + componentCodeName.substring(0, 2);
            componentName = componentCodeName.substring(2);
        }

        BomQrCodeAddDto bomQrCodeAddDto = BomQrCodeAddDto.builder().partOrderCode(qrCodeAddReq.getPartOrderCode()).
                projectCode(qrCodeAddReq.getProjectCode()).
                componentCode(componentCode).
                componentName(componentName).
                partCode(partCode).
                partName(partName).
                partVersion(qrCodeAddReq.getPartVersion()).
                partNumber(qrCodeAddReq.getPartNumber()).
                paperNumber(qrCodeAddReq.getPaperNumber()).
                rawMaterial(qrCodeAddReq.getRawMaterial()).
                surfaceTreatment(qrCodeAddReq.getSurfaceTreatment()).
                weight(qrCodeAddReq.getWeight()).
                designer(qrCodeAddReq.getDesigner()).purchaseReason(qrCodeAddReq.getPurchaseReason()).
                companyType(qrCodeAddReq.getCompanyType()).build();

        bomManager.addFromQrCode(bomQrCodeAddDto);
        return AjaxResult.success();
    }

    /**
     * 新增BOM列表
     */
    @Log(title = "BOM", businessType = BusinessType.INSERT)
    @PostMapping("/byImport")
    public AjaxResult addBomListByImport(@RequestBody List<BomImportDTO> importDtoList) {
        bomManager.addBom(importDtoList);
        return AjaxResult.success("新增成功");
    }

    /**
     * 修改机加工单导致的修改BOM
     */
    @Log(title = "BOM", businessType = BusinessType.UPDATE)
    @PutMapping("/updateByPartOrder")
    public AjaxResult updateBomByPartOrder(@RequestBody @Validated List<BomEditByPartOrderReq> bomEditByPartOrderReqList) {
        bomManager.updateBomByPartOrder(bomEditByPartOrderReqList);
        return AjaxResult.success("新增成功");
    }
}