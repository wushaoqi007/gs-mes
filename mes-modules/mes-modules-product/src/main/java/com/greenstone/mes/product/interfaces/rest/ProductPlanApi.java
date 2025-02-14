package com.greenstone.mes.product.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.product.application.dto.cmd.ProductPlanStatusChangeCmd;
import com.greenstone.mes.product.application.service.ProductPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/tables/100000116")
public class ProductPlanApi extends BaseController {

    private final ProductPlanService productPlanService;


    @PutMapping("/statusChange")
    public AjaxResult statusChange(@Validated @RequestBody ProductPlanStatusChangeCmd statusChangeCmd) {
        productPlanService.statusChange(statusChangeCmd);
        return AjaxResult.success();
    }

}
