package com.greenstone.mes.asset.interfaces.rest;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetDeleteCmd;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetImportCmd;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetInsertCmd;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetUpdateCmd;
import com.greenstone.mes.asset.application.dto.cqe.query.AssetFuzzyQuery;
import com.greenstone.mes.asset.application.dto.result.AssetExportResult;
import com.greenstone.mes.asset.application.service.AssetService;
import com.greenstone.mes.asset.domain.entity.Asset;
import com.greenstone.mes.common.core.utils.poi.ExcelUtil;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.log.annotation.ApiLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/6 15:39
 */
@Slf4j
@RestController
@RequestMapping("/asset")
public class AssetApi extends BaseController {

    private final AssetService assetService;

    public AssetApi(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping
    public TableDataInfo list() {
        startPage();
        return getDataTable(assetService.list());
    }

    @GetMapping("/my")
    public AjaxResult myAsset(AssetFuzzyQuery query) {
        List<String> fields = new ArrayList<>();
        query.setFields(fields);
        fields.add("barCode");
        fields.add("typeCode");
        fields.add("typeHierarchy");
        fields.add("name");
        fields.add("sn");
        fields.add("specification");
        fields.add("receivedBy");
        List<Asset> assets = assetService.queryMyAsset(query);
        return AjaxResult.success(assets);
    }

    @PostMapping("/fuzzy")
    public TableDataInfo fuzzyQuery(@Validated @RequestBody AssetFuzzyQuery query) {
        startPage();
        List<String> fields = new ArrayList<>();
        query.setFields(fields);
        fields.add("barCode");
        fields.add("typeCode");
        fields.add("typeHierarchy");
        fields.add("name");
        fields.add("sn");
        fields.add("specification");
        fields.add("receivedBy");
        return getDataTable(assetService.fuzzyQuery(query));
    }

    @PostMapping
    public AjaxResult insert(@Validated @RequestBody AssetInsertCmd saveCmd) {
        assetService.insert(saveCmd);
        return AjaxResult.success();
    }

    @PutMapping
    public AjaxResult update(@Validated @RequestBody AssetUpdateCmd updateCmd) {
        assetService.update(updateCmd);
        return AjaxResult.success();
    }

    @DeleteMapping
    public AjaxResult delete(@Validated @RequestBody AssetDeleteCmd deleteCmd) {
        assetService.remove(deleteCmd);
        return AjaxResult.success();
    }

    @PostMapping("/export")
    public void export(HttpServletResponse response, @Validated @RequestBody AssetFuzzyQuery query) {
        List<String> fields = new ArrayList<>();
        query.setFields(fields);
        fields.add("barCode");
        fields.add("typeCode");
        fields.add("typeHierarchy");
        fields.add("name");
        fields.add("sn");
        fields.add("specification");
        fields.add("receivedBy");
        List<AssetExportResult> assets = assetService.exportResults(query);
        ExcelUtil<AssetExportResult> util = new ExcelUtil<>(AssetExportResult.class);
        util.exportExcel(response, assets, "资产数据" + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss"));
    }

    @PostMapping("/import")
    @ApiLog
    public AjaxResult importAssets(MultipartFile file) {
        // 将表格内容转为对象
        ExcelUtil<AssetImportCmd> util = new ExcelUtil<>(AssetImportCmd.class);
        List<AssetImportCmd> assetImportCmds;
        try {
            assetImportCmds = util.importExcel(file.getInputStream());
            log.info("Import assets size: {}.", assetImportCmds.size());
            if (CollectionUtil.isEmpty(assetImportCmds)) {
                log.error("There is no asset in file!");
                return AjaxResult.error("导入的数据不能为空");
            }
        } catch (Exception e) {
            log.error("无法解析导入数据", e);
            return AjaxResult.error("无法解析导入数据，请联系管理员。");
        }
        assetService.importAssets(assetImportCmds);
        return AjaxResult.success();
    }
}
