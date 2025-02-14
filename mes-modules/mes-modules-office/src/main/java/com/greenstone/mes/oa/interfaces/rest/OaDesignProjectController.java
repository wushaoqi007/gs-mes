package com.greenstone.mes.oa.interfaces.rest;

import com.greenstone.mes.common.core.utils.bean.BeanUtils;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.log.annotation.Log;
import com.greenstone.mes.common.log.enums.BusinessType;
import com.greenstone.mes.common.security.annotation.RequiresPermissions;
import com.greenstone.mes.oa.application.service.OaDesignProjectService;
import com.greenstone.mes.oa.domain.OaDesignProject;
import com.greenstone.mes.oa.request.OaDesignProjectAddReq;
import com.greenstone.mes.oa.request.OaDesignProjectEditReq;
import com.greenstone.mes.oa.request.OaDesignProjectListReq;
import com.greenstone.mes.oa.response.OaDesignProjectListResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 设计部门项目Controller
 *
 * @author gu_renkai
 * @date 2022-05-24
 */
@RestController
@RequestMapping("/design-project")
@Slf4j
public class OaDesignProjectController extends BaseController {

    @Autowired
    private OaDesignProjectService oaDesignProjectService;

    /**
     * 查询设计部门项目列表
     */
    @GetMapping("/list")
    public TableDataInfo list(OaDesignProjectListReq listReq) {
        OaDesignProject project = new OaDesignProject();
        BeanUtils.copyBeanProp(project, listReq);
        startPage();
        List<OaDesignProject> projects = oaDesignProjectService.listPlus(project);
        TableDataInfo dataTable = getDataTable(projects);
        List<OaDesignProjectListResp> respList = oaDesignProjectService.getProjects(projects);
        dataTable.setData(respList);
        return dataTable;
    }

    /**
     * 获取设计部门项目详细信息
     */
    @GetMapping(value = "/{code}")
    public AjaxResult getInfo(@PathVariable("code") String code) {
        return AjaxResult.success(oaDesignProjectService.getProject(code));
    }

    /**
     * 新增设计部门项目
     */
    @Log(title = "设计部门项目", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody List<OaDesignProjectAddReq> addReqList) {
        oaDesignProjectService.saveProject(addReqList);
        return AjaxResult.success();
    }

    /**
     * 修改设计部门项目
     */
    @Log(title = "设计部门项目", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody List<OaDesignProjectEditReq> editReqList) {
        oaDesignProjectService.updateProject(editReqList);
        return AjaxResult.success();
    }

    /**
     * 删除设计部门项目
     */
    @Log(title = "设计部门项目", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(oaDesignProjectService.removeByIds(Arrays.asList(ids)));
    }
}