package com.greenstone.mes.table.domain.service;

import com.greenstone.mes.table.domain.entity.NoticeData;
import com.greenstone.mes.table.infrastructure.persistence.ItemNotice;

public interface ItemNoticeService {


    void sendNotice(NoticeData noticeData);

}
