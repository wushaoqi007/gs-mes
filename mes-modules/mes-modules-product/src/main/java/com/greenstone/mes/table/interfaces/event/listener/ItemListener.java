package com.greenstone.mes.table.interfaces.event.listener;

import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.table.TableEntity;
import com.greenstone.mes.table.TablePo;
import com.greenstone.mes.table.core.TableThreadLocal;
import com.greenstone.mes.table.domain.entity.NoticeData;
import com.greenstone.mes.table.domain.service.ItemNoticeService;
import com.greenstone.mes.table.domain.service.ItemStreamService;
import com.greenstone.mes.table.infrastructure.constant.TableConst;
import com.greenstone.mes.table.interfaces.event.ItemCreateEvent;
import com.greenstone.mes.table.interfaces.event.ItemUpdateEvent;
import com.greenstone.mes.table.interfaces.event.ItemWasteEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ItemListener<E extends TableEntity, P extends TablePo> {

    private final ItemStreamService itemStreamService;
    private final ItemNoticeService itemNoticeService;

    @Async
    @EventListener
    public void onItemCreate(ItemCreateEvent<E, P> itemCreateEvent) {
        sendNotice(itemCreateEvent.getItem(), itemCreateEvent.getOperator(), TableConst.Rights.CREATE, itemCreateEvent.getTableMeta());
    }

    @Async
    @EventListener(ItemUpdateEvent.class)
    public void onItemUpdate(ItemUpdateEvent<E, P> itemUpdateEvent) {
        itemStreamService.updateStream(itemUpdateEvent.getItem(), itemUpdateEvent.getOldItem());

        sendNotice(itemUpdateEvent.getItem(), itemUpdateEvent.getOperator(), TableConst.Rights.UPDATE, itemUpdateEvent.getTableMeta());
    }

    @Async
    @EventListener
    public void onItemWaste(ItemWasteEvent<E, P> itemWasteEvent) {
        sendNotice(itemWasteEvent.getItem(), itemWasteEvent.getOperator(), TableConst.Rights.DELETE, itemWasteEvent.getTableMeta());
    }

    private void sendNotice(TableEntity item, User operator, String action, TableThreadLocal.ActionMeta<E, P> tableMeta) {
        NoticeData noticeData = new NoticeData();
        noticeData.setFunctionId(tableMeta.getFunctionId());
        noticeData.setItemId(item.getId());
        noticeData.setItemAction(tableMeta.getAction());
        noticeData.setSerialNo(item.getSerialNo());
        noticeData.setActionUser(operator);
        noticeData.setFunctionName(tableMeta.getFunctionName());
        noticeData.setItemAction(action);
        noticeData.setItemId(item.getId());
        noticeData.setSerialNo(item.getSerialNo());
        itemNoticeService.sendNotice(noticeData);
    }

}
