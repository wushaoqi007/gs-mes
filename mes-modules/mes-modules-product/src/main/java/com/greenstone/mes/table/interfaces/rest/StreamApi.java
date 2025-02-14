package com.greenstone.mes.table.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.table.TableEntity;
import com.greenstone.mes.table.TablePo;
import com.greenstone.mes.table.core.FunctionModel;
import com.greenstone.mes.table.core.FunctionServiceHelper;
import com.greenstone.mes.table.domain.service.ItemStreamService;
import com.greenstone.mes.table.infrastructure.persistence.ItemStream;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tables/{tableId}/streams")
public class StreamApi<E extends TableEntity, P extends TablePo> extends BaseController {

    private final FunctionServiceHelper<E, P> serviceHelper;
    private final ItemStreamService streamService;

    @PostMapping("/view")
    public List<ItemStream> getEntities(@PathVariable("tableId") String tableId, HttpServletRequest request) {
        FunctionModel<E, P> model = serviceHelper.getService(tableId);
        E e = bodyToObject(model.getEntityClass(), request);
        return streamService.getStreams(e);
    }

}
