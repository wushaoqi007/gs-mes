package com.greenstone.mes.system.application.service;

import com.greenstone.mes.system.application.dto.cmd.ParamAddCmd;
import com.greenstone.mes.system.application.dto.cmd.ParamDataAddCmd;
import com.greenstone.mes.system.application.dto.cmd.ParamMoveCmd;
import com.greenstone.mes.system.application.dto.query.ParamDataQuery;
import com.greenstone.mes.system.application.dto.query.ParamQuery;
import com.greenstone.mes.system.application.dto.result.ParamResult;
import com.greenstone.mes.system.domain.entity.ParamData;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-03-11-15:38
 */
public interface ParamService {
    // param
    List<ParamResult> selectParamList(ParamQuery query);

    ParamResult selectParamByType(String paramName);

    void saveParam(ParamAddCmd cmd);

    void updateParam(ParamAddCmd cmd);

    void deleteParamByIds(String[] paramIds);

    void resetParamCache();

    void clearParamCache();

    void loadingParamCache();

    // detail
    List<ParamData> selectParamDataByParamType(String paramId);

    void saveData(ParamDataAddCmd cmd);

    void updateData(ParamDataAddCmd cmd);

    void deleteParamDataByIds(String[] detailIds);

    List<ParamData> selectParamDataList(ParamDataQuery query);

    void moveParamData(ParamMoveCmd sortCmd);
}
