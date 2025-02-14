package com.greenstone.mes.material.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.log.annotation.ApiLog;
import com.greenstone.mes.material.domain.entity.MaterialAdditional;
import com.greenstone.mes.material.domain.service.MaterialAdditionalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/material/additional")
public class MaterialAdditionalController extends BaseController {

    @Autowired
    private MaterialAdditionalService materialAdditionalService;

    /**
     * 零件信息补录
     *
     * @param additional 零件信息
     */
    @ApiLog
    @PostMapping
    public AjaxResult add(@RequestBody @Validated MaterialAdditional additional) {
        materialAdditionalService.commit(additional);
        return AjaxResult.success("操作成功");
    }

}