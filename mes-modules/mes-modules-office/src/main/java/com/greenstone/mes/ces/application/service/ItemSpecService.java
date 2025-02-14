package com.greenstone.mes.ces.application.service;

import com.greenstone.mes.ces.application.dto.cmd.ItemSpecAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.ItemSpecEditCmd;
import com.greenstone.mes.ces.application.dto.cmd.ItemSpecRemoveCmd;
import com.greenstone.mes.ces.application.dto.event.ItemTypeAddE;
import com.greenstone.mes.ces.application.dto.event.ItemTypeRemoveE;
import com.greenstone.mes.ces.application.dto.query.ItemSpecFuzzyQuery;
import com.greenstone.mes.ces.application.dto.query.ItemSpecQuery;
import com.greenstone.mes.ces.application.dto.result.ItemSpecResult;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-05-22-10:41
 */
public interface ItemSpecService {

    List<ItemSpecResult> list(ItemSpecQuery query);

    void add(ItemSpecAddCmd addCmd);

    void edit(ItemSpecEditCmd editCmd);

    void remove(ItemSpecRemoveCmd removeCmd);

    void typeAddEvent(ItemTypeAddE addE);

    void typeRemoveEvent(ItemTypeRemoveE removeE);

    List<ItemSpecResult> search(ItemSpecFuzzyQuery query);
}
