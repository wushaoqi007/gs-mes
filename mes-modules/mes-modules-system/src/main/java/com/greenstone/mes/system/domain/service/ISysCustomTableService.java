package com.greenstone.mes.system.domain.service;

import com.greenstone.mes.common.mybatisplus.IServiceWrapper;
import com.greenstone.mes.system.domain.SysCustomTable;
import com.greenstone.mes.system.dto.cmd.SysCustomTableAddReq;
import com.greenstone.mes.system.dto.query.SysCustomTableListReq;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2022-10-31-8:36
 */
public interface ISysCustomTableService extends IServiceWrapper<SysCustomTable> {
    void addSysCustomTable(SysCustomTableAddReq customTableAddReq);

    List<SysCustomTable> selectCustomTableList(SysCustomTableListReq customTableListReq);

    void resetSysCustomTable(SysCustomTableListReq customTableListReq);
}
