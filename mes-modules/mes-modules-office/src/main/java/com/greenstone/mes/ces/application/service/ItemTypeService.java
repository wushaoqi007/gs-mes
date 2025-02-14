package com.greenstone.mes.ces.application.service;

import com.greenstone.mes.ces.application.dto.cmd.ItemTypeAddCmd;
import com.greenstone.mes.ces.application.dto.result.ItemTypeResult;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-05-22-10:38
 */
public interface ItemTypeService {

    void add(ItemTypeAddCmd addCmd);

    void remove(String typeCode);

    List<ItemTypeResult> list();
}
