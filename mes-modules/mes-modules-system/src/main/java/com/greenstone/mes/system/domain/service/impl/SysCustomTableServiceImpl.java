package com.greenstone.mes.system.domain.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.system.domain.SysCustomTable;
import com.greenstone.mes.system.domain.SysCustomTableDefault;
import com.greenstone.mes.system.domain.service.ISysCustomTableDefaultService;
import com.greenstone.mes.system.domain.service.ISysCustomTableService;
import com.greenstone.mes.system.dto.cmd.SysCustomTableAddReq;
import com.greenstone.mes.system.dto.query.SysCustomTableListReq;
import com.greenstone.mes.system.infrastructure.mapper.SysCustomTableMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2022-10-31-8:37
 */
@Slf4j
@Service
public class SysCustomTableServiceImpl extends ServiceImpl<SysCustomTableMapper, SysCustomTable> implements ISysCustomTableService {

    @Autowired
    private ISysCustomTableDefaultService customTableDefaultService;

    @Override
    @Transactional
    public void addSysCustomTable(SysCustomTableAddReq customTableAddReq) {
        // 新增自定义列之前，先删除该用户原配置
        SysCustomTable sysCustomTable = SysCustomTable.builder().tableName(customTableAddReq.getTableName()).userId(SecurityUtils.getUserId()).build();
        QueryWrapper<SysCustomTable> queryWrapper = Wrappers.query(sysCustomTable);
        remove(queryWrapper);
        if (CollUtil.isNotEmpty(customTableAddReq.getColumnList())) {
            for (SysCustomTableAddReq.ColumnInfo columnInfo : customTableAddReq.getColumnList()) {
                SysCustomTable addInfo = SysCustomTable.builder()
                        .tableName(customTableAddReq.getTableName()).userId(SecurityUtils.getUserId())
                        .columnName(columnInfo.getColumnName()).width(columnInfo.getWidth())
                        .columnNameCn(columnInfo.getColumnNameCn())
                        .isFilter(columnInfo.getIsFilter())
                        .isShow(columnInfo.getIsShow()).isNecessary(columnInfo.getIsNecessary())
                        .sort(columnInfo.getSort()).build();
                save(addInfo);
            }
        }
    }

    @Override
    public List<SysCustomTable> selectCustomTableList(SysCustomTableListReq customTableListReq) {
        // 查询该用户的自定义列表配置
        SysCustomTable sysCustomTable = SysCustomTable.builder().tableName(customTableListReq.getTableName()).userId(SecurityUtils.getUserId()).build();
        QueryWrapper<SysCustomTable> queryWrapper = Wrappers.query(sysCustomTable);
        queryWrapper.orderByDesc("sort");
        List<SysCustomTable> customTableList = list(queryWrapper);
        if (CollUtil.isEmpty(customTableList)) {
            // 如果该用户没有配置，取默认配置表配置
            SysCustomTableDefault customTableDefault = SysCustomTableDefault.builder().tableName(customTableListReq.getTableName()).build();
            QueryWrapper<SysCustomTableDefault> defaultQueryWrapper = Wrappers.query(customTableDefault);
            defaultQueryWrapper.orderByDesc("sort");
            List<SysCustomTableDefault> defaultList = customTableDefaultService.list(defaultQueryWrapper);
            if (CollUtil.isEmpty(defaultList)) {
                log.info("system custom table no default config,table name:{}", customTableListReq.getTableName());
                throw new ServiceException("自定义表格没有默认配置，表格名：" + customTableListReq.getTableName());
            } else {
                log.info("system custom table select from default,{}", defaultList);
                List<SysCustomTable> customTables = new ArrayList<>();
                for (SysCustomTableDefault sysCustomTableDefault : defaultList) {
                    customTables.add(SysCustomTable.builder().tableName(sysCustomTableDefault.getTableName())
                            .width(sysCustomTableDefault.getWidth()).isShow(sysCustomTableDefault.getIsShow())
                            .isNecessary(sysCustomTableDefault.getIsNecessary()).sort(sysCustomTableDefault.getSort())
                            .isFilter(sysCustomTableDefault.getIsFilter())
                            .columnName(sysCustomTableDefault.getColumnName()).columnNameCn(sysCustomTableDefault.getColumnNameCn()).build());
                }
                return customTables;
            }
        }
        return customTableList;
    }

    @Override
    @Transactional
    public void resetSysCustomTable(SysCustomTableListReq customTableListReq) {
        log.info("system custom table reset start");
        // 先删除该用户原配置
        SysCustomTable sysCustomTable = SysCustomTable.builder().tableName(customTableListReq.getTableName()).userId(SecurityUtils.getUserId()).build();
        QueryWrapper<SysCustomTable> queryWrapper = Wrappers.query(sysCustomTable);
        remove(queryWrapper);
        // 查询默认配置
        SysCustomTableDefault customTableDefault = SysCustomTableDefault.builder().tableName(customTableListReq.getTableName()).build();
        QueryWrapper<SysCustomTableDefault> defaultQueryWrapper = Wrappers.query(customTableDefault);
        List<SysCustomTableDefault> defaultList = customTableDefaultService.list(defaultQueryWrapper);
        if (CollUtil.isEmpty(defaultList)) {
            log.info("system custom table no default config,table name:{}", customTableListReq.getTableName());
            throw new ServiceException("自定义表格没有默认配置，表格名：" + customTableListReq.getTableName());
        } else {
            // 新增该用户配置为默认配置
            log.info("system custom table reset start add,{}", defaultList);
            for (SysCustomTableDefault sysCustomTableDefault : defaultList) {
                SysCustomTable customTable = SysCustomTable.builder().userId(SecurityUtils.getUserId()).tableName(sysCustomTableDefault.getTableName())
                        .width(sysCustomTableDefault.getWidth()).isShow(sysCustomTableDefault.getIsShow()).isFilter(sysCustomTableDefault.getIsFilter())
                        .isNecessary(sysCustomTableDefault.getIsNecessary()).sort(sysCustomTableDefault.getSort()).
                        columnName(sysCustomTableDefault.getColumnName()).columnNameCn(sysCustomTableDefault.getColumnNameCn()).build();
                save(customTable);
            }
        }
    }

}
