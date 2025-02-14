package com.greenstone.mes.ces.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.ces.application.dto.query.ApplicationFuzzyQuery;
import com.greenstone.mes.ces.domain.converter.ApplicationConverter;
import com.greenstone.mes.ces.domain.entity.CesApplication;
import com.greenstone.mes.ces.domain.entity.CesApplicationItem;
import com.greenstone.mes.ces.domain.entity.ItemSpec;
import com.greenstone.mes.ces.dto.cmd.AppStatusChangeCmd;
import com.greenstone.mes.ces.infrastructure.mapper.CesApplicationItemMapper;
import com.greenstone.mes.ces.infrastructure.mapper.CesApplicationMapper;
import com.greenstone.mes.ces.infrastructure.persistence.CesApplicationDO;
import com.greenstone.mes.ces.infrastructure.persistence.CesApplicationItemDO;
import com.greenstone.mes.common.core.enums.FormError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author gu_renkai
 * @date 2023/2/21 16:22
 */
@Service
@AllArgsConstructor
public class CesApplicationRepository {

    private final CesApplicationMapper applicationMapper;
    private final CesApplicationItemMapper itemMapper;
    private final ApplicationConverter converter;
    private final ItemSpecRepository itemSpecRepository;
    private final RemoteSystemService systemService;

    public CesApplication get(String serialNo) {
        return converter.toCesApplication(applicationMapper.getOneOnly(CesApplicationDO.builder().serialNo(serialNo).build()));
    }

    public void statusChange(AppStatusChangeCmd statusChangeCmd) {
        LambdaUpdateWrapper<CesApplicationDO> updateWrapper = Wrappers.lambdaUpdate(CesApplicationDO.class).set(CesApplicationDO::getStatus, statusChangeCmd.getStatus())
                .in(CesApplicationDO::getSerialNo, statusChangeCmd.getSerialNos());
        applicationMapper.update(updateWrapper);
    }

    public CesApplication detail(String serialNo) {
        CesApplicationDO select = CesApplicationDO.builder().serialNo(serialNo).build();
        CesApplicationDO application = applicationMapper.getOneOnly(select);
        if (application == null) {
            throw new ServiceException("选择的申请单不存在，请重新选择");
        }
        List<CesApplicationItemDO> itemDOS = itemMapper.list(CesApplicationItemDO.builder().serialNo(serialNo).build());
        CesApplication cesApplication = converter.toCesApplication(application, itemDOS);
        setTypeName(cesApplication);
        return cesApplication;
    }

    public List<CesApplication> list(ApplicationFuzzyQuery fuzzyQuery) {
        QueryWrapper<CesApplicationDO> fuzzyQueryWrapper = new QueryWrapper<>();
        fuzzyQueryWrapper.orderByDesc("create_time");
        if (StrUtil.isNotEmpty(fuzzyQuery.getKey()) && CollUtil.isNotEmpty(fuzzyQuery.getFields())) {
            fuzzyQueryWrapper.and(wrapper -> {
                for (String field : fuzzyQuery.getFields()) {
                    String fieldCode = StrUtil.toSymbolCase(field, '_');
                    wrapper.or().like(fieldCode, fuzzyQuery.getKey());
                }
            });
        }
        if (Objects.nonNull(fuzzyQuery.getState())) {
            fuzzyQueryWrapper.eq("state", fuzzyQuery.getState());
        }
        List<CesApplication> applications = new ArrayList<>();
        List<CesApplicationDO> applicationDOS = applicationMapper.selectList(fuzzyQueryWrapper);
        for (CesApplicationDO applicationDO : applicationDOS) {
            List<CesApplicationItemDO> itemDOS = itemMapper.list(CesApplicationItemDO.builder().serialNo(applicationDO.getSerialNo()).build());
            CesApplication cesApplication = converter.toCesApplication(applicationDO, itemDOS);
            setTypeName(cesApplication);
            applications.add(cesApplication);
        }
        return applications;
    }

    public void setTypeName(CesApplication cesApplication) {
        for (CesApplicationItem item : cesApplication.getItems()) {
            if (item.getItemCode() != null) {
                ItemSpec itemSpec = itemSpecRepository.detail(item.getItemCode());
                if (itemSpec != null) {
                    item.setTypeName(itemSpec.getTypeName());
                }
            }
        }
    }

    public void add(CesApplication application) {
        CesApplicationDO applicationDO = converter.toCesApplicationDO(application);
        List<CesApplicationItemDO> itemDOS = converter.toCesApplicationItemDOs(application.getItems());
        applicationMapper.insert(applicationDO);
        for (CesApplicationItemDO itemDO : itemDOS) {
            itemDO.setSerialNo(applicationDO.getSerialNo());
        }
        itemMapper.insertBatchSomeColumn(itemDOS);
    }

    public void edit(CesApplication application) {
        CesApplicationDO applicationDO = converter.toCesApplicationDO(application);
        List<CesApplicationItemDO> itemDOS = converter.toCesApplicationItemDOs(application.getItems());

        applicationMapper.updateById(applicationDO);
        itemMapper.delete(CesApplicationItemDO.builder().serialNo(applicationDO.getSerialNo()).build());
        itemMapper.insertBatchSomeColumn(itemDOS);
    }

    public CesApplication updateNum(CesApplication application) {
        CesApplication needNotice = null;
        List<CesApplicationItem> needNoticeItems = new ArrayList<>();
        for (CesApplicationItem updateItem : application.getItems()) {
            if (Objects.isNull(updateItem.getId())) {
                continue;
            }
            CesApplicationItemDO findItemDO = itemMapper.getOneOnly(CesApplicationItemDO.builder().id(updateItem.getId()).build());
            if (Objects.nonNull(findItemDO)) {
                if (needNotice == null) {
                    needNotice = get(findItemDO.getSerialNo());
                }
                if (Objects.nonNull(updateItem.getPurchasedNum())) {
                    findItemDO.setPurchasedNum(findItemDO.getPurchasedNum() == null ? updateItem.getPurchasedNum() : updateItem.getPurchasedNum() + findItemDO.getPurchasedNum());
                }
                if (Objects.nonNull(updateItem.getReadyNum())) {
                    long allReadNum = findItemDO.getReadyNum() == null ? updateItem.getReadyNum() : updateItem.getReadyNum() + findItemDO.getReadyNum();
                    findItemDO.setReadyNum(allReadNum);
                    if (allReadNum >= findItemDO.getItemNum()) {
                        needNoticeItems.add(converter.toCesApplicationItem(findItemDO));
                    }
                }
                if (Objects.nonNull(updateItem.getProvidedNum())) {
                    findItemDO.setProvidedNum(findItemDO.getProvidedNum() == null ? updateItem.getProvidedNum() : updateItem.getProvidedNum() + findItemDO.getProvidedNum());
                }
                itemMapper.updateById(findItemDO);
            }
        }
        if (needNotice != null) {
            needNotice.setItems(needNoticeItems);
        }
        return needNotice;
    }

    public void changeStatus(CesApplication application) {
        LambdaUpdateWrapper<CesApplicationDO> updateWrapper = Wrappers.lambdaUpdate(CesApplicationDO.class)
                .eq(CesApplicationDO::getSerialNo, application.getSerialNo())
                .set(CesApplicationDO::getStatus, application.getStatus());
        applicationMapper.update(updateWrapper);
    }

    public void remove(List<String> serialNos) {
        for (String serialNo : serialNos) {
            CesApplicationDO appFound = applicationMapper.getOneOnly(CesApplicationDO.builder().serialNo(serialNo).build());
            if (appFound == null) {
                throw new ServiceException(FormError.E70101);
            }
            if (appFound.getStatus() != ProcessStatus.DRAFT) {
                throw new ServiceException(FormError.E70102);
            }
        }

        LambdaQueryWrapper<CesApplicationDO> appWrapper = Wrappers.lambdaQuery(CesApplicationDO.class).in(CesApplicationDO::getSerialNo, serialNos);
        applicationMapper.delete(appWrapper);
        LambdaQueryWrapper<CesApplicationItemDO> itemWrapper = Wrappers.lambdaQuery(CesApplicationItemDO.class).in(CesApplicationItemDO::getSerialNo,
                serialNos);
        itemMapper.delete(itemWrapper);
    }


    public List<CesApplication> waitHandle(ApplicationFuzzyQuery query) {
        LambdaQueryWrapper<CesApplicationDO> queryWrapper = Wrappers.lambdaQuery(CesApplicationDO.class)
                .in(CesApplicationDO::getStatus, List.of(ProcessStatus.APPROVED, ProcessStatus.ISSUED));
        return converter.toCesApplicationList(applicationMapper.selectList(queryWrapper));
    }
}
