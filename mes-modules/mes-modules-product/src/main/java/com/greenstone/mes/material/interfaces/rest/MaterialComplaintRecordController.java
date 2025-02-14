package com.greenstone.mes.material.interfaces.rest;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.log.annotation.Log;
import com.greenstone.mes.common.log.enums.BusinessType;
import com.greenstone.mes.common.security.annotation.RequiresPermissions;
import com.greenstone.mes.material.domain.MaterialComplaintRecord;
import com.greenstone.mes.material.application.service.MaterialComplaintRecordManager;
import com.greenstone.mes.material.request.MaterialComplaintRecordAddReq;
import com.greenstone.mes.material.request.MaterialComplaintRecordEditReq;
import com.greenstone.mes.material.domain.service.IMaterialComplaintRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 投诉记录接口
 *
 * @author wushaoqi
 * @date 2022-10-21-14:27
 */
@Slf4j
@RestController
@RequestMapping("/complaint")
public class MaterialComplaintRecordController extends BaseController {

    @Autowired
    private MaterialComplaintRecordManager complaintRecordManager;

    @Autowired
    private IMaterialComplaintRecordService complaintRecordService;

    /**
     * 新增投诉（PC）
     */
    @Log(title = "新增投诉", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated MaterialComplaintRecordAddReq complaintRecordAddReq) {
        // 组件号处理：如果是两位数字需要拼接项目代码-组件号
        if (StrUtil.isNumeric(complaintRecordAddReq.getComponentCode()) && complaintRecordAddReq.getComponentCode().length() == 2) {
            complaintRecordAddReq.setComponentCode(complaintRecordAddReq.getProjectCode() + "-" + complaintRecordAddReq.getComponentCode());
        }
        complaintRecordManager.addComplaintRecord(complaintRecordAddReq);
        return AjaxResult.success("新增成功");
    }

    /**
     * 新增投诉(小程序)
     */
    @Log(title = "新增投诉", businessType = BusinessType.INSERT)
    @PostMapping("/addFromApp")
    public AjaxResult addFromApp(@RequestBody @Validated MaterialComplaintRecordAddReq complaintRecordAddReq) {
        // 组件号处理：如果是两位数字需要拼接项目代码-组件号
        if (StrUtil.isNumeric(complaintRecordAddReq.getComponentCode()) && complaintRecordAddReq.getComponentCode().length() == 2) {
            complaintRecordAddReq.setComponentCode(complaintRecordAddReq.getProjectCode() + "-" + complaintRecordAddReq.getComponentCode());
        }
        complaintRecordManager.addComplaintRecord(complaintRecordAddReq);
        return AjaxResult.success("新增成功");
    }

    /**
     * 投诉列表查询
     */
    @GetMapping("/list")
    public TableDataInfo selectComplaintRecordList(MaterialComplaintRecord complaintRecord) {
        startPage();
        List<MaterialComplaintRecord> list = complaintRecordManager.selectComplaintRecordList(complaintRecord);
        return getDataTable(list);
    }

    /**
     * 获取零件投诉记录详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getComplaintInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(complaintRecordService.getById(id));
    }

    /**
     * 获取零件投诉对应的质检记录人员
     */
    @GetMapping(value = "/inspector/list/{id}")
    public AjaxResult getInspectionList(@PathVariable("id") Long id) {
        return AjaxResult.success(complaintRecordManager.getInspectionList(id));
    }

    /**
     * 投诉确认
     */
    @Log(title = "投诉确认", businessType = BusinessType.UPDATE)
    @PutMapping("/confirm")
    public AjaxResult changeStatus(@RequestBody @Validated MaterialComplaintRecordEditReq complaintRecordEditReq) {
        complaintRecordManager.confirmComplaint(complaintRecordEditReq);
        return AjaxResult.success("更新成功");
    }
}
