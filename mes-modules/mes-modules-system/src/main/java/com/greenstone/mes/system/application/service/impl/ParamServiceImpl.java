package com.greenstone.mes.system.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.TreeUtils;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.system.application.assembler.ParamAssembler;
import com.greenstone.mes.system.application.dto.cmd.ParamAddCmd;
import com.greenstone.mes.system.application.dto.cmd.ParamDataAddCmd;
import com.greenstone.mes.system.application.dto.cmd.ParamMoveCmd;
import com.greenstone.mes.system.application.dto.query.ParamDataQuery;
import com.greenstone.mes.system.application.dto.query.ParamQuery;
import com.greenstone.mes.system.application.dto.result.ParamResult;
import com.greenstone.mes.system.application.service.ParamService;
import com.greenstone.mes.system.domain.entity.ParamData;
import com.greenstone.mes.system.domain.entity.ParamType;
import com.greenstone.mes.system.domain.repository.ParamRepository;
import com.greenstone.mes.system.infrastructure.util.ParamUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-03-11-15:38
 */
@AllArgsConstructor
@Slf4j
@Service
public class ParamServiceImpl implements ParamService {

    private final ParamRepository paramRepository;
    private final ParamAssembler assembler;

    /**
     * 项目启动时，初始化字典到缓存
     */
    @PostConstruct
    public void init() {
        loadingParamCache();
    }

    @Override
    public void loadingParamCache() {
        List<ParamType> paramTypeList = paramRepository.selectParamAll();
        for (ParamType paramType : paramTypeList) {
            setParamCache(paramType.getParamType());
        }
    }

    @Override
    public void moveParamData(ParamMoveCmd sortCmd) {
        List<ParamData> childParamDataList = paramRepository.selectParamDataListByParentId(sortCmd.getParamType(), sortCmd.getParentId());
        if (CollUtil.isEmpty(childParamDataList)) {
            throw new ServiceException(StrUtil.format("移动失败，参数类型{}下不存在父级id：{}", sortCmd.getParamType(), sortCmd.getParentId()));
        }
        List<ParamData> sortedParamDataList = childParamDataList.stream().filter(data -> !data.getId().equals(sortCmd.getDataId())).sorted(Comparator.comparing(ParamData::getOrderNum)).toList();
        for (int i = 0; i < sortedParamDataList.size(); i++) {
            ParamData paramData = sortedParamDataList.get(i);
            int order = sortCmd.getOrderNum() > i ? i : i + 1;
            if (order != paramData.getOrderNum()) {
                // 在这之后的菜单排序需要 +1
                paramData.setOrderNum(order);
                paramRepository.updateParamDataSort(paramData);
                log.debug("menu {} {} update with order num {}.", paramData.getId(), paramData.getParamValue1(), order);
            } else {
                log.debug("menu {} {} no need update", paramData.getId(), paramData.getParamValue1());
            }
        }
        paramRepository.updateParamDataById(ParamData.builder().id(sortCmd.getDataId()).parentId(sortCmd.getParentId()).orderNum(sortCmd.getOrderNum()).build());
        log.info("SysMenuServiceImpl.setMenuOrder: end");
        // 刷新缓存
        setParamCache(sortCmd.getParamType());
    }

    @Override
    public List<ParamResult> selectParamList(ParamQuery query) {
        List<ParamType> results = paramRepository.selectParamList(query);
        return assembler.toParamRs(results);
    }

    @Override
    public ParamResult selectParamByType(String paramType) {
        if (StrUtil.isBlank(paramType)) {
            throw new ServiceException("参数类型不为空");
        }
        ParamType find = paramRepository.selectParamByType(paramType);
        find.setParamDataList(selectParamDataByParamType(paramType));
        return assembler.toParamR(find);
    }

    @Override
    public List<ParamData> selectParamDataByParamType(String paramType) {
        List<ParamData> paramDataList = ParamUtils.getParamCache(paramType);
        if (CollUtil.isNotEmpty(paramDataList)) {
            return paramDataList;
        }
        paramDataList = TreeUtils.toTree(paramRepository.selectParamDataByParamType(paramType));
        if (CollUtil.isNotEmpty(paramDataList)) {
            ParamUtils.setParamCache(paramType, paramDataList);
            return paramDataList;
        }
        return null;
    }

    @Override
    public List<ParamData> selectParamDataList(ParamDataQuery query) {
        List<ParamData> paramDataList = ParamUtils.getParamCache(query.getParamType());
        if (paramDataList != null && CollUtil.isNotEmpty(paramDataList)) {
            if (StrUtil.isEmpty(query.getParamValue())) {
                return paramDataList;
            }
            return filterData(query.getParamValue(), paramDataList);
        }
        paramDataList = TreeUtils.toTree(paramRepository.selectParamDataList(query));
        if (CollUtil.isNotEmpty(paramDataList)) {
            ParamUtils.setParamCache(query.getParamType(), paramDataList);
            return paramDataList;
        }
        return null;
    }

    public List<ParamData> filterData(String paramValue, List<ParamData> paramDataList) {
        List<ParamData> filter = new ArrayList<>();
        for (ParamData paramData : paramDataList) {
            if (CollUtil.isNotEmpty(paramData.getChildren())) {
                filter.addAll(filterData(paramValue, paramData.getChildren()));
            }
            if (paramData.getParamValue1().contains(paramValue)) {
                paramData.setChildren(null);
                filter.add(paramData);
            }

        }
        return filter;
    }

    @Override
    public void saveParam(ParamAddCmd cmd) {
        ParamType paramType = toSaveEntity(cmd);
        int row = paramRepository.saveParamType(paramType);
        if (row > 0) {
            ParamUtils.setParamCache(paramType.getParamType(), null);
        }
    }

    @Override
    public void updateParam(ParamAddCmd cmd) {
        if (StrUtil.isBlank(cmd.getId())) {
            throw new ServiceException("id不为空");
        }
        ParamType paramType = toSaveEntity(cmd);
        int row = paramRepository.updateParamType(paramType);
        if (row > 0) {
            setParamCache(paramType.getParamType());
        }
    }

    @Override
    public void saveData(ParamDataAddCmd cmd) {
        ParamData detail = assembler.toParamData(cmd);
        int row = paramRepository.saveOrUpdateData(detail);
        if (row > 0) {
            setParamCache(cmd.getParamType());
        }
    }

    @Override
    public void updateData(ParamDataAddCmd cmd) {
        if (StrUtil.isBlank(cmd.getId())) {
            throw new ServiceException("id不为空");
        }
        saveData(cmd);
    }

    @Override
    public void deleteParamDataByIds(String[] dataIds) {
        for (String dataId : dataIds) {
            ParamData paramData = paramRepository.selectParamDataById(dataId);
            // 删除子级
            deleteParamDetailChild(paramData);
            paramRepository.deleteParamDetailById(paramData.getId());
            setParamCache(paramData.getParamType());
        }
    }


    public void setParamCache(String paramType) {
        List<ParamData> paramDataList = TreeUtils.toTree(paramRepository.selectParamDataByParamType(paramType));
        ParamUtils.setParamCache(paramType, paramDataList);
    }

    public void deleteParamDetailChild(ParamData paramData) {
        List<ParamData> children = paramRepository.selectParamDataListByParentId(paramData.getParamType(), paramData.getId());
        if (CollUtil.isNotEmpty(children)) {
            for (ParamData child : children) {
                // 递归删除
                deleteParamDetailChild(child);
                paramRepository.deleteParamDetailById(child.getId());
            }
        }
    }

    @Override
    public void resetParamCache() {
        clearParamCache();
        loadingParamCache();
    }

    @Override
    public void clearParamCache() {
        ParamUtils.clearParamCache();
    }

    public ParamType toSaveEntity(ParamAddCmd cmd) {
        ParamType paramType = assembler.toParam(cmd);
        paramType.setDeptId(SecurityUtils.getLoginUser().getUser().getDeptId());
        paramType.setCreateById(SecurityUtils.getLoginUser().getUser().getUserId());
        if (StrUtil.isBlank(paramType.getStatus())) {
            paramType.setStatus("0");
        }
        return paramType;
    }

    @Override
    public void deleteParamByIds(String[] paramIds) {
        for (String paramId : paramIds) {
            ParamType paramType = paramRepository.selectParamById(paramId);
            paramRepository.deleteParam(paramType);
            ParamUtils.removeParamCache(paramType.getParamType());
        }
    }

}
