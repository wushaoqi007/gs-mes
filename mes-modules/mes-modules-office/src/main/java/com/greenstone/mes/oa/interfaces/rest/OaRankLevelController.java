package com.greenstone.mes.oa.interfaces.rest;

import com.greenstone.mes.common.core.utils.poi.ExcelUtil;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.log.annotation.Log;
import com.greenstone.mes.common.log.enums.BusinessType;
import com.greenstone.mes.common.security.annotation.RequiresPermissions;
import com.greenstone.mes.oa.application.manager.OaRankLevelManager;
import com.greenstone.mes.oa.application.service.OaRankLevelService;
import com.greenstone.mes.oa.domain.OaRankLevel;
import com.greenstone.mes.oa.request.OaRankLevelAddReq;
import com.greenstone.mes.oa.request.OaRankLevelEditReq;
import com.greenstone.mes.oa.request.OaRankLevelUserRelationListReq;
import com.greenstone.mes.oa.response.OaRankLevelResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 职能等级接口
 *
 * @author wushaoqi
 * @date 2022-05-31-14:25
 */
@Slf4j
@RestController
@RequestMapping("/rankLevel")
public class OaRankLevelController extends BaseController {

    @Autowired
    private OaRankLevelService oaRankLevelService;

    @Autowired
    private OaRankLevelManager oaRankLevelManager;


    /**
     * 添加技能等级信息
     */
    @Log(title = "职级管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody @Validated OaRankLevelAddReq oaRankLevelAddReq) {
        oaRankLevelService.addRankLevel(oaRankLevelAddReq);
        return AjaxResult.success("新增成功");
    }

    /**
     * 修改技能等级信息
     */
    @Log(title = "职级管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated OaRankLevelEditReq oaRankLevelEditReq) {
        oaRankLevelService.updateRankLevel(oaRankLevelEditReq);
        return AjaxResult.success("更新成功");
    }

    /**
     * 查询技能等级信息列表
     */
    @GetMapping("/treeselect")
    public AjaxResult treeselect(OaRankLevel oaRankLevel) {
        List<OaRankLevelResp> rankLevelList = oaRankLevelService.selectRankLevelList(oaRankLevel);
        return AjaxResult.success(oaRankLevelService.buildRankLevelTreeSelect(rankLevelList));
    }

    /**
     * 查询技能等级信息详情
     */
    @GetMapping(value = "/{id}")
    public AjaxResult rankLevelDetail(@PathVariable("id") Long id) {
        return AjaxResult.success(oaRankLevelService.getRecordDetail(id));
    }

    /**
     * 删除职级等级
     */
    @Log(title = "职级管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        if (oaRankLevelManager.checkRankLevelExistRelation(id)) {
            return AjaxResult.error("职级存在用户关联,不允许删除");
        }
        oaRankLevelManager.deleteRankLevelById(id);
        return AjaxResult.success("删除成功");
    }

    /**
     * 导出
     */
    @PostMapping("/export/rank")
    public void exportRankLevel(HttpServletResponse response, @RequestBody OaRankLevelUserRelationListReq rankLevel) {
        XSSFWorkbook workbook = oaRankLevelManager.exportRankLevel(rankLevel);
        new ExcelUtil<>().writeToHttp(response, workbook, "生产技能等级.xlsx");
    }
}
