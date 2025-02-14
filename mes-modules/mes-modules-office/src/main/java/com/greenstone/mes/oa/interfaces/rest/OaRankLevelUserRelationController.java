package com.greenstone.mes.oa.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.log.annotation.Log;
import com.greenstone.mes.common.log.enums.BusinessType;
import com.greenstone.mes.common.security.annotation.RequiresPermissions;
import com.greenstone.mes.oa.application.manager.OaRankLevelManager;
import com.greenstone.mes.oa.application.service.OaRankLevelUserRelationService;
import com.greenstone.mes.oa.request.OaRankLevelUserRelationAddReq;
import com.greenstone.mes.oa.request.OaRankLevelUserRelationEditReq;
import com.greenstone.mes.oa.request.OaRankLevelUserRelationListReq;
import com.greenstone.mes.oa.response.OaRankLevelUserRelationListResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 职级与人员关系接口
 *
 * @author wushaoqi
 * @date 2022-06-01-9:12
 */
@Slf4j
@RestController
@RequestMapping("/rankLevel/userRelation")
public class OaRankLevelUserRelationController extends BaseController {

    @Autowired
    private OaRankLevelUserRelationService oaRankLevelUserRelationService;

    @Autowired
    private OaRankLevelManager oaRankLevelManager;


    /**
     * 添加技能等级关联人员信息
     */
    @Log(title = "职级关系管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody @Validated OaRankLevelUserRelationAddReq oaRankLevelUserRelationAddReq) {
        oaRankLevelManager.addRankLevelUserRelation(oaRankLevelUserRelationAddReq);
        return AjaxResult.success("新增成功");
    }

    /**
     * 修改技能等级关联人员信息
     */
    @Log(title = "职级关系管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated OaRankLevelUserRelationEditReq oaRankLevelUserRelationEditReq) {
        oaRankLevelManager.updateRankLevelUserRelation(oaRankLevelUserRelationEditReq);
        return AjaxResult.success("更新成功");
    }

    /**
     * 查询技能等级和人员关联信息列表
     */
    @GetMapping("/list")
    public TableDataInfo getList(OaRankLevelUserRelationListReq oaRankLevelUserRelationListReq) {
        startPage();
        List<OaRankLevelUserRelationListResp> list = oaRankLevelUserRelationService.selectRankLevelUserRelationList(oaRankLevelUserRelationListReq);
        return getDataTable(list);
    }

    /**
     * 获取技能等级和人员关联信息详情
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(oaRankLevelUserRelationService.selectRankLevelUserRelationById(id));
    }

    /**
     * 删除人员和职级关系
     */
    @Log(title = "职级关系管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable("id") Long id) {
        return toAjax(oaRankLevelManager.deleteRankLevelUserRelationById(id));
    }

}
