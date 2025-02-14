package com.greenstone.mes.system.domain.repository;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.system.application.dto.query.ParamDataQuery;
import com.greenstone.mes.system.application.dto.query.ParamQuery;
import com.greenstone.mes.system.domain.converter.ParamConverter;
import com.greenstone.mes.system.domain.entity.ParamData;
import com.greenstone.mes.system.domain.entity.ParamType;
import com.greenstone.mes.system.infrastructure.mapper.ParamDataMapper;
import com.greenstone.mes.system.infrastructure.mapper.ParamTypeMapper;
import com.greenstone.mes.system.infrastructure.po.ParamDataDO;
import com.greenstone.mes.system.infrastructure.po.ParamTypeDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-03-11-15:42
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ParamRepository {
    private final ParamTypeMapper paramTypeMapper;
    private final ParamDataMapper paramDataMapper;
    private final ParamConverter converter;

    public List<ParamType> selectParamAll() {
        List<ParamTypeDO> paramTypeDOS = paramTypeMapper.selectList(Wrappers.query(ParamTypeDO.builder().build()));
        return converter.dos2Entities(paramTypeDOS);
    }

    public ParamType selectParamById(String paramId) {
        ParamTypeDO paramTypeDO = paramTypeMapper.selectById(paramId);
        if (paramTypeDO == null) {
            throw new ServiceException(StrUtil.format("参数配置未找到，参数类型id:{}", paramId));
        }
        return converter.do2Entity(paramTypeDO);
    }

    public ParamType selectParamByType(String paramType) {
        ParamTypeDO paramTypeDO = paramTypeMapper.getOneOnly(ParamTypeDO.builder().paramType(paramType).build());
        if (paramTypeDO == null) {
            throw new ServiceException(StrUtil.format("参数配置未找到，参数类型:{}", paramType));
        }
        return converter.do2Entity(paramTypeDO);
    }

    public List<ParamType> selectParamList(ParamQuery query) {
        LambdaQueryWrapper<ParamTypeDO> queryWrapper = Wrappers.lambdaQuery(ParamTypeDO.class)
                .eq(StrUtil.isNotEmpty(query.getParamType()), ParamTypeDO::getParamType, query.getParamType())
                .like(StrUtil.isNotEmpty(query.getParamName()), ParamTypeDO::getParamName, query.getParamName())
                .eq(StrUtil.isNotEmpty(query.getStatus()), ParamTypeDO::getStatus, query.getStatus())
                .le(query.getEndTime() != null, ParamTypeDO::getCreateTime, query.getEndTime())
                .ge(query.getBeginTime() != null, ParamTypeDO::getCreateTime, query.getBeginTime())
                .orderByDesc(ParamTypeDO::getCreateTime);
        List<ParamTypeDO> paramTypeDOS = paramTypeMapper.selectList(queryWrapper);
        return converter.dos2Entities(paramTypeDOS);
    }

    public int saveParamType(ParamType paramType) {
        ParamTypeDO saveDO = converter.entity2Do(paramType);
        // 重名校验
        ParamTypeDO getByType = paramTypeMapper.getOneOnly(ParamTypeDO.builder().paramType(paramType.getParamType()).build());
        if (getByType != null && !getByType.getId().equals(paramType.getId())) {
            throw new ServiceException(StrUtil.format("参数保存失败，已存在同名参数，参数类型：{}", paramType.getParamType()));
        }
        return paramTypeMapper.insert(saveDO);
    }

    public int updateParamType(ParamType paramType) {
        ParamTypeDO saveDO = converter.entity2Do(paramType);
        // 重名校验
        ParamTypeDO getByType = paramTypeMapper.getOneOnly(ParamTypeDO.builder().paramType(paramType.getParamType()).build());
        if (getByType != null && !getByType.getId().equals(paramType.getId())) {
            throw new ServiceException(StrUtil.format("参数保存失败，已存在同名参数，参数类型：{}", paramType.getParamType()));
        }
        ParamTypeDO find = paramTypeMapper.selectById(saveDO.getId());
        if (find == null) {
            throw new ServiceException(StrUtil.format("参数更新失败，参数未找到，id:{}", saveDO.getId()));
        }
        // 更新参数数据
        if (!find.getParamType().equals(saveDO.getParamType())) {
            LambdaUpdateWrapper<ParamDataDO> updateWrapper = Wrappers.lambdaUpdate(ParamDataDO.class)
                    .eq(ParamDataDO::getParamType, find.getParamType())
                    .set(ParamDataDO::getParamType, saveDO.getParamType());
            paramDataMapper.update(updateWrapper);
        }
        return paramTypeMapper.updateById(saveDO);


    }

    public void deleteParam(ParamType paramType) {
        paramTypeMapper.deleteById(paramType.getId());
        LambdaQueryWrapper<ParamDataDO> deleteParamDetail = Wrappers.lambdaQuery(ParamDataDO.class)
                .eq(ParamDataDO::getParamType, paramType.getParamType());
        paramDataMapper.delete(deleteParamDetail);
    }

    public int saveOrUpdateData(ParamData data) {
        ParamDataDO saveDO = converter.dataEntity2Do(data);
        ParamType paramType = selectParamByType(saveDO.getParamType());
        saveDO.setParamType(paramType.getParamType());
        if (StrUtil.isEmpty(saveDO.getParentId())) {
            saveDO.setParentId(paramType.getId());
        } else {
            // 父参数校验
            ParamDataDO parent = paramDataMapper.selectById(saveDO.getParentId());
            ParamTypeDO paramTypeDO = paramTypeMapper.selectById(saveDO.getParentId());
            if (parent == null) {
                if (paramTypeDO == null) {
                    throw new ServiceException(StrUtil.format("参数值保存失败，父参数未找到，父参数id:{}", saveDO.getParentId()));
                }
                if (!paramTypeDO.getParamType().equals(saveDO.getParamType())) {
                    throw new ServiceException(StrUtil.format("参数值保存失败，与父参数类型不一致，父参数类型:{}，参数类型：{}", paramTypeDO.getParamType(), saveDO.getParamType()));
                }
            } else {
                if (!parent.getParamType().equals(saveDO.getParamType())) {
                    throw new ServiceException(StrUtil.format("参数值保存失败，与父参数类型不一致，父参数类型:{}，参数类型：{}", parent.getParamType(), saveDO.getParamType()));
                }
            }
        }
        // 重值校验
        ParamDataDO getByValue = paramDataMapper.getOneOnly(ParamDataDO.builder().paramType(saveDO.getParamType()).paramValue1(saveDO.getParamValue1()).build());
        if (getByValue != null && !getByValue.getId().equals(saveDO.getId())) {
            throw new ServiceException(StrUtil.format("参数值保存失败，{}已存在同名参数值：{}", saveDO.getParamType(), getByValue.getParamValue1()));
        }
        if (saveDO.getId() != null) {
            ParamDataDO find = paramDataMapper.selectById(saveDO.getId());
            if (find == null) {
                throw new ServiceException(StrUtil.format("参数值更新失败，参数值未找到，id:{}", saveDO.getId()));
            }
            saveDO.setOrderNum(find.getOrderNum());
            return paramDataMapper.updateById(saveDO);
        }
        // 设置排序
        Long existNumber = paramDataMapper.selectCount(ParamDataDO.builder().parentId(saveDO.getParentId()).build());
        saveDO.setOrderNum(existNumber.intValue());
        return paramDataMapper.insert(saveDO);
    }

    public void updateParamDataSort(ParamData data) {
        LambdaUpdateWrapper<ParamDataDO> updateWrapper = Wrappers.lambdaUpdate(ParamDataDO.class)
                .eq(ParamDataDO::getId, data.getId()).set(ParamDataDO::getOrderNum, data.getOrderNum());
        paramDataMapper.update(updateWrapper);
    }

    public void updateParamDataById(ParamData data) {
        paramDataMapper.updateById(ParamDataDO.builder().id(data.getId()).parentId(data.getParentId()).orderNum(data.getOrderNum()).build());
    }

    public List<ParamData> selectParamDataByParamType(String paramType) {
        LambdaQueryWrapper<ParamDataDO> queryWrapper = Wrappers.lambdaQuery(ParamDataDO.class)
                .eq(ParamDataDO::getParamType, paramType).orderByAsc(ParamDataDO::getOrderNum);
        List<ParamDataDO> dataDOS = paramDataMapper.selectList(queryWrapper);
        return converter.dataDos2Entities(dataDOS);
    }

    public ParamData selectParamDataById(String dataId) {
        ParamDataDO paramDataDO = paramDataMapper.selectById(dataId);
        if (paramDataDO == null) {
            throw new ServiceException(StrUtil.format("参数值未找到，id：{}", dataId));
        }
        return converter.dataDo2Entity(paramDataDO);
    }

    public List<ParamData> selectParamDataListByParentId(String paramType, String parentId) {
        LambdaQueryWrapper<ParamDataDO> queryWrapper = Wrappers.lambdaQuery(ParamDataDO.class)
                .eq(ParamDataDO::getParamType, paramType)
                .eq(ParamDataDO::getParentId, parentId);
        List<ParamDataDO> dataDOS = paramDataMapper.selectList(queryWrapper);
        return converter.dataDos2Entities(dataDOS);
    }

    public void deleteParamDetailById(String id) {
        paramDataMapper.deleteById(id);
    }

    public List<ParamData> selectParamDataList(ParamDataQuery query) {
        log.info("查询系统参数数据：{}", query);
        LambdaQueryWrapper<ParamDataDO> queryWrapper = Wrappers.lambdaQuery(ParamDataDO.class)
                .eq(ParamDataDO::getParamType, query.getParamType())
                .like(StrUtil.isNotEmpty(query.getParamValue()), ParamDataDO::getParamValue1, query.getParamValue())
                .orderByAsc(ParamDataDO::getOrderNum);
        List<ParamDataDO> dataDOS = paramDataMapper.selectList(queryWrapper);
        return converter.dataDos2Entities(dataDOS);
    }
}
