package com.greenstone.mes.table.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.system.api.domain.SysFile;
import com.greenstone.mes.table.TableEntity;
import com.greenstone.mes.table.TablePo;
import com.greenstone.mes.table.core.FunctionModel;
import com.greenstone.mes.table.core.FunctionServiceHelper;
import com.greenstone.mes.table.domain.service.ItemStreamService;
import com.greenstone.mes.table.infrastructure.persistence.ItemStream;
import com.greenstone.mes.table.interfaces.rest.cmd.ItemDelete;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tables/{tableId}/items")
public class ItemApi<E extends TableEntity, P extends TablePo> extends BaseController {

    private final FunctionServiceHelper<E, P> serviceHelper;
    private final ItemStreamService streamService;

    @GetMapping("/drafts")
    public List<?> getDrafts(@PathVariable("tableId") String tableId) {
        FunctionModel<E, P> model = serviceHelper.getService(tableId);
        return model.getTableService().getDrafts();
    }

    @GetMapping("{itemId}")
    public Object getEntity(@PathVariable("tableId") String tableId, @PathVariable("itemId") Long itemId) {
        FunctionModel<E, P> model = serviceHelper.getService(tableId);
        return model.getTableService().getEntity(itemId);
    }

    @PostMapping("/view")
    public TableDataInfo getEntities(@PathVariable("tableId") String tableId, HttpServletRequest request) {
        startPage();
        FunctionModel<E, P> model = serviceHelper.getService(tableId);
        E e = bodyToObject(model.getEntityClass(), request);
        return getDataTable(model.getTableService().getEntities(e));
    }

    @Transactional
    @PostMapping
    public E create(@PathVariable("tableId") String tableId, HttpServletRequest request) {
        FunctionModel<E, P> model = serviceHelper.getService(tableId);
        E e = bodyToObject(model.getEntityClass(), request);
        return model.getTableService().create(e);
    }

    @Transactional
    @PutMapping("/{itemId}")
    public E update(@PathVariable("tableId") String tableId, @PathVariable("itemId") Long itemId, HttpServletRequest request) {
        FunctionModel<E, P> model = serviceHelper.getService(tableId);
        E e = bodyToObject(model.getEntityClass(), request);
        return model.getTableService().update(e, null);
    }

    @Transactional
    @PostMapping("/submit")
    public E submit(@PathVariable("tableId") String tableId, HttpServletRequest request) {
        FunctionModel<E, P> model = serviceHelper.getService(tableId);
        E e = bodyToObject(model.getEntityClass(), request);
        return model.getTableService().submit(e);
    }

    @Transactional
    @DeleteMapping("{itemId}")
    public void delete(@PathVariable("tableId") String tableId, @PathVariable("itemId") Long itemId) {
        FunctionModel<E, P> model = serviceHelper.getService(tableId);
        model.getTableService().delete(itemId);
    }

    @Transactional
    @DeleteMapping("/batchDelete")
    public void batchDelete(@PathVariable("tableId") String tableId, @RequestBody @Validated ItemDelete itemDelete) {
        FunctionModel<E, P> model = serviceHelper.getService(tableId);
        model.getTableService().batchDelete(itemDelete.getIds());
    }

    @Transactional
    @PostMapping("/{itemId}/lock")
    public void lock(@PathVariable("tableId") String tableId, @PathVariable("itemId") Long itemId) {
        FunctionModel<E, P> model = serviceHelper.getService(tableId);
        model.getTableService().lock(itemId);
    }

    @Transactional
    @PostMapping("/{itemId}/unlock")
    public void unlock(@PathVariable("tableId") String tableId, @PathVariable("itemId") Long itemId) {
        FunctionModel<E, P> model = serviceHelper.getService(tableId);
        model.getTableService().unlock(itemId);
    }

    @Transactional
    @PostMapping("/import")
    public void importData(@PathVariable("tableId") String tableId, @RequestParam Map<String, Object> params, MultipartFile file) {
        FunctionModel<E, P> model = serviceHelper.getService(tableId);
        model.getTableService().importData(file, params);
    }

    @PostMapping("/export")
    public SysFile exportData(@PathVariable("tableId") String tableId, HttpServletRequest request) {
        FunctionModel<E, P> model = serviceHelper.getService(tableId);
        E e = bodyToObject(model.getEntityClass(), request);
        return model.getTableService().exportData(e);
    }

    @GetMapping("/{itemId}/stream")
    public List<ItemStream> stream(@PathVariable("tableId") String tableId, @PathVariable("itemId") Long itemId) {
        return streamService.getStreams(TableEntity.builder().id(itemId).build());
    }


}
