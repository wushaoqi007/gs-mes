package com.greenstone.mes.market.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.enums.FormError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.external.dto.result.MailSendResult;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.market.application.dto.query.MarketAppFuzzyQuery;
import com.greenstone.mes.market.domain.converter.MarketAppConverter;
import com.greenstone.mes.market.domain.entity.MarketApplication;
import com.greenstone.mes.market.infrastructure.mapper.MarketApplicationAttachmentMapper;
import com.greenstone.mes.market.infrastructure.mapper.MarketApplicationMapper;
import com.greenstone.mes.market.infrastructure.persistence.MarketAppAttachmentDo;
import com.greenstone.mes.market.infrastructure.persistence.MarketAppDo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Slf4j
@Service
public class MarketAppRepository {

    private final MarketApplicationMapper applicationMapper;
    private final MarketApplicationAttachmentMapper attachmentMapper;
    private final MarketAppConverter converter;


    public MarketApplication get(String serialNo) {
        MarketAppDo appDo = applicationMapper.getOneOnly(MarketAppDo.builder().serialNo(serialNo).build());
        return converter.toMarketApplication(appDo);
    }

    public MarketApplication getById(String id) {
        MarketAppDo appDo = applicationMapper.selectById(id);
        if (appDo == null) {
            throw new ServiceException(FormError.E70101);
        }
        List<MarketAppAttachmentDo> attachmentDos = attachmentMapper.list(MarketAppAttachmentDo.builder().serialNo(appDo.getSerialNo()).build());
        return converter.toMarketApplication(appDo, attachmentDos);
    }

    public MarketApplication detail(String serialNo) {
        MarketAppDo appDo = applicationMapper.getOneOnly(MarketAppDo.builder().serialNo(serialNo).build());
        if (appDo == null) {
            throw new ServiceException(FormError.E70101);
        }
        List<MarketAppAttachmentDo> attachmentDos = attachmentMapper.list(MarketAppAttachmentDo.builder().serialNo(serialNo).build());
        return converter.toMarketApplication(appDo, attachmentDos);
    }

    public List<MarketApplication> list(MarketAppFuzzyQuery fuzzyQuery) {
        QueryWrapper<MarketAppDo> fuzzyQueryWrapper = new QueryWrapper<>();
        fuzzyQueryWrapper.orderByDesc("create_time");
        if (StrUtil.isNotEmpty(fuzzyQuery.getKey()) && CollUtil.isNotEmpty(fuzzyQuery.getFields())) {
            fuzzyQueryWrapper.and(wrapper -> {
                for (String field : fuzzyQuery.getFields()) {
                    String fieldCode = StrUtil.toSymbolCase(field, '_');
                    wrapper.or().like(fieldCode, fuzzyQuery.getKey());
                }
            });
        }
        List<MarketApplication> applications = new ArrayList<>();
        List<MarketAppDo> applicationDOS = applicationMapper.selectList(fuzzyQueryWrapper);
        for (MarketAppDo appDo : applicationDOS) {
            List<MarketAppAttachmentDo> attachmentDos = attachmentMapper.list(MarketAppAttachmentDo.builder().serialNo(appDo.getSerialNo()).build());
            applications.add(converter.toMarketApplication(appDo, attachmentDos));
        }
        return applications;
    }

    public void save(MarketApplication marketApplication) {
        if (StrUtil.isEmpty(marketApplication.getId())) {
            MarketAppDo marketAppDo = converter.toMarketApplicationDo(marketApplication);
            applicationMapper.insert(marketAppDo);
            List<MarketAppAttachmentDo> attachmentDos = converter.toMarketApplicationAttachmentDos(marketApplication.getAttachments());
            attachmentMapper.insertBatchSomeColumn(attachmentDos);
            marketApplication.setId(marketAppDo.getId());
        } else {
            MarketAppDo marketAppDo = converter.toMarketApplicationDo(marketApplication);
            List<MarketAppAttachmentDo> attachmentDos = converter.toMarketApplicationAttachmentDos(marketApplication.getAttachments());
            for (MarketAppAttachmentDo attachmentDo : attachmentDos) {
                attachmentDo.setSerialNo(marketAppDo.getSerialNo());
            }
            applicationMapper.update(marketAppDo, Wrappers.lambdaQuery(MarketAppDo.class).eq(MarketAppDo::getSerialNo, marketAppDo.getSerialNo()));
            attachmentMapper.delete(MarketAppAttachmentDo.builder().serialNo(marketAppDo.getSerialNo()).build());
            attachmentMapper.insertBatchSomeColumn(attachmentDos);
        }

    }

    public void updateMarketApplication(MarketApplication marketApplication) {
        MarketAppDo marketAppDo = converter.toMarketApplicationDo(marketApplication);
        applicationMapper.updateById(marketAppDo);
    }

    public void delete(List<String> serialNos) {
        LambdaQueryWrapper<MarketAppDo> wrapper = Wrappers.lambdaQuery(MarketAppDo.class).in(MarketAppDo::getSerialNo, serialNos);
        applicationMapper.delete(wrapper);
        LambdaQueryWrapper<MarketAppAttachmentDo> attachmentWrapper = Wrappers.lambdaQuery(MarketAppAttachmentDo.class).in(MarketAppAttachmentDo::getSerialNo, serialNos);
        attachmentMapper.delete(attachmentWrapper);
    }

    public void changeStatus(List<String> serialNos, ProcessStatus status) {
        LambdaUpdateWrapper<MarketAppDo> wrapper = Wrappers.lambdaUpdate(MarketAppDo.class).set(MarketAppDo::getStatus, status)
                .in(MarketAppDo::getSerialNo, serialNos);
        applicationMapper.update(wrapper);
    }

    public void changeStatus(String serialNo, ProcessStatus status) {
        LambdaUpdateWrapper<MarketAppDo> wrapper = Wrappers.lambdaUpdate(MarketAppDo.class).set(MarketAppDo::getStatus, status)
                .eq(MarketAppDo::getSerialNo, serialNo);
        applicationMapper.update(wrapper);
    }

    public MarketApplication getBySpNo(String spNo) {
        MarketAppDo appDo = applicationMapper.getOneOnly(MarketAppDo.builder().spNo(spNo).build());
        if (appDo == null) {
            throw new ServiceException(StrUtil.format("审批失败，未找到市购件申请单，审批编号：{}", spNo));
        }
        List<MarketAppAttachmentDo> attachmentDOS = attachmentMapper.list(MarketAppAttachmentDo.builder().serialNo(appDo.getSerialNo()).build());
        MarketApplication marketApplication = converter.toMarketApplication(appDo);
        marketApplication.setAttachments(converter.toMarketApplicationAttachments(attachmentDOS));
        return marketApplication;
    }

    public void saveMailResult(MailSendResult mailSendResult) {
        LambdaUpdateWrapper<MarketAppDo> updateWrapper = Wrappers.lambdaUpdate(MarketAppDo.class)
                .set(MarketAppDo::getMailStatus, mailSendResult.getStatus())
                .set(MarketAppDo::getMailMsg, mailSendResult.getErrorMsg())
                .eq(MarketAppDo::getSerialNo, mailSendResult.getSerialNo());
        applicationMapper.update(updateWrapper);
    }
}
