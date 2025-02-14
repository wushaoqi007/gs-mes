package com.greenstone.mes.table.domain.service;

import com.greenstone.mes.table.TableEntity;
import com.greenstone.mes.table.infrastructure.persistence.ItemStream;

import java.util.List;

public interface ItemStreamService {

    <E extends TableEntity> List<ItemStream> getStreams(E e);

    <E extends TableEntity> void updateStream(E entity, E oldEntity);

}
