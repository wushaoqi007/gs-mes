package com.greenstone.mes.table.core;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.table.TableEntity;
import com.greenstone.mes.table.TablePo;

public interface TableExpress<E extends TableEntity, P extends TablePo, M extends EasyBaseMapper<P>> {

    void notice();

    void logStream(E d);

}
