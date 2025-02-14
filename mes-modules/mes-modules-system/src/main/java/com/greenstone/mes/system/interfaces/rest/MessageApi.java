package com.greenstone.mes.system.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.system.domain.service.MessageService;
import com.greenstone.mes.system.dto.cmd.MessageDeleteCmd;
import com.greenstone.mes.system.dto.cmd.MessageReadCmd;
import com.greenstone.mes.system.dto.cmd.MessageSaveCmd;
import com.greenstone.mes.system.dto.query.MessageListQuery;
import com.greenstone.mes.system.dto.result.MessageListResult;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/4/7 14:56
 */

@AllArgsConstructor
@RestController
@RequestMapping("/message")
public class MessageApi extends BaseController {

    private final MessageService messageService;

    @GetMapping("/unread")
    public AjaxResult unread() {
        return AjaxResult.success(messageService.getUnreadCount());
    }

    @GetMapping("/{id}")
    public AjaxResult get(@PathVariable("id") String id) {
        return AjaxResult.success(messageService.get(id));
    }

    @GetMapping
    public TableDataInfo list(MessageListQuery query) {
        startPage();
        List<MessageListResult> messages = messageService.list(query);
        return getDataTable(messages);
    }

    @PostMapping
    public AjaxResult save(@RequestBody @Validated MessageSaveCmd saveCmd) {
        messageService.save(saveCmd);
        return AjaxResult.success();
    }

    @PutMapping
    public AjaxResult read(@RequestBody @Validated MessageReadCmd readCmd) {
        messageService.read(readCmd.getIds());
        return AjaxResult.success();
    }

    @PutMapping("/readAll")
    public AjaxResult readAll() {
        messageService.readAll();
        return AjaxResult.success();
    }

    @DeleteMapping
    public AjaxResult delete(@RequestBody @Validated MessageDeleteCmd deleteCmd) {
        messageService.delete(deleteCmd.getIds());
        return AjaxResult.success();
    }

    @DeleteMapping("/deleteAll")
    public AjaxResult deleteAll() {
        messageService.deleteAll();
        return AjaxResult.success();
    }

}