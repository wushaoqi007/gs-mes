package com.greenstone.mes.table.interfaces.event;

import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.table.TableEntity;
import com.greenstone.mes.table.TablePo;
import com.greenstone.mes.table.core.TableThreadLocal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemUpdateEvent<E extends TableEntity, P extends TablePo> {

    private E item;

    private E oldItem;

    private User operator;

    private TableThreadLocal.ActionMeta<E, P> tableMeta;

}
