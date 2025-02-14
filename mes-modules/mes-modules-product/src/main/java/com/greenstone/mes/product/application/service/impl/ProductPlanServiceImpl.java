package com.greenstone.mes.product.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.ValidationUtils;
import com.greenstone.mes.common.core.utils.poi.ExcelUtil;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.product.application.dto.cmd.ProductPlanImportVO;
import com.greenstone.mes.product.application.dto.cmd.ProductPlanStatusChangeCmd;
import com.greenstone.mes.product.application.dto.result.ProductPlanExportR;
import com.greenstone.mes.product.application.service.ProductPlanService;
import com.greenstone.mes.product.domain.entity.ProductPlan;
import com.greenstone.mes.product.domain.entity.ProductPlanChangeReason;
import com.greenstone.mes.product.domain.repository.ProductPlanRepository;
import com.greenstone.mes.product.infrastructure.constant.ProductConst;
import com.greenstone.mes.product.infrastructure.enums.ProductPlanLevel;
import com.greenstone.mes.product.infrastructure.enums.ProductPlanStatus;
import com.greenstone.mes.product.infrastructure.enums.ProductPlanType;
import com.greenstone.mes.product.infrastructure.mapper.ProductPlanMapper;
import com.greenstone.mes.product.infrastructure.persistence.ProductPlanDO;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.table.core.AbstractTableService;
import com.greenstone.mes.table.core.TableRepository;
import com.greenstone.mes.table.infrastructure.annotation.TableFunction;
import com.greenstone.mes.table.infrastructure.constant.UpdateReason;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@TableFunction(id = "100000116", entityClass = ProductPlan.class, poClass = ProductPlanDO.class, updateReason = UpdateReason.NECESSARY, reasonClass = ProductPlanChangeReason.class)
@Service
public class ProductPlanServiceImpl extends AbstractTableService<ProductPlan, ProductPlanDO, ProductPlanMapper> implements ProductPlanService {

    private final ProductPlanRepository planRepository;
    private final RemoteSystemService systemService;

    public ProductPlanServiceImpl(TableRepository<ProductPlan, ProductPlanDO, ProductPlanMapper> tableRepository,
                                  ApplicationEventPublisher eventPublisher, ProductPlanRepository planRepository,
                                  RemoteSystemService systemService) {
        super(tableRepository, eventPublisher);
        this.planRepository = planRepository;
        this.systemService = systemService;
    }

    @Override
    public void importData(MultipartFile file, Map<String, Object> params) {
        log.info("Receive product plan import request");
        if (CollUtil.isEmpty(params) || params.get("planType") == null) {
            throw new ServiceException("请选择导入的计划类型");
        }
        ProductPlanType planType = ProductPlanType.getByType(params.get("planType").toString());
        // 将表格转为VO
        List<ProductPlanImportVO> importVOs = new ExcelUtil<>(ProductPlanImportVO.class).toList(file);
        importVOs.forEach(ProductPlanImportVO::trim);
        // 校验表格数据
        String validateResult = ValidationUtils.validate(importVOs);
        if (Objects.nonNull(validateResult)) {
            log.error(validateResult);
            throw new ServiceException(validateResult);
        }
        importProductPlan(importVOs, planType);
    }

    @Override
    public String generateSerialNo(ProductPlan plan) {
        if (StrUtil.isNotBlank(plan.getSerialNo())) {
            // 项目级别的计划编号不可重复
            if (Objects.equals(ProductPlanLevel.PROJECT.getLevel(), plan.getLevel()) && planRepository.isExist(plan.getSerialNo())) {
                throw new ServiceException(StrUtil.format("计划编号已存在：{}，请使用新的计划编号", plan.getSerialNo()));
            }
            return plan.getSerialNo();
        } else {
            SerialNoNextCmd nextCmd =
                    SerialNoNextCmd.builder().type("product_plan").prefix("ZPP" + DateUtil.yearMonthSerialStrNow()).build();
            String serialNo = systemService.nextShortSn(nextCmd).getSerialNo();
            // 校验编码是否可用
            while (planRepository.isExist(serialNo)) {
                log.info("计划编号：{}重复，重新获取中", serialNo);
                serialNo = systemService.nextShortSn(nextCmd).getSerialNo();
            }
            return serialNo;
        }
    }

    @Override
    public void beforeCreate(ProductPlan plan) {
        if (Objects.equals(ProductPlanLevel.PROJECT.getLevel(), plan.getLevel())) {
            // 是否有顶层计划，没有则创建
            ProductPlan topPlan = planRepository.selectTopPlan(plan);
            plan.setParentId(topPlan.getId());
        }
        plan.setPlanStatus(ProductPlanStatus.NOT_STARTED.getStatus());
        validateGeneral(plan);
        validateCreate(plan);
    }

    @Override
    public void beforeUpdate(ProductPlan plan) {
        validateGeneral(plan);
        validateUpdate(plan);
    }

    @Override
    public void afterCreate(ProductPlan plan) {
        // 更新父节点进度（完成率）
        calculateCompletionRate(plan, null);
    }

    @Override
    public void afterDelete(ProductPlan plan) {
        // 更新父节点进度（完成率）
        calculateCompletionRate(plan, null);
    }

    public void calculateCompletionRate(ProductPlan plan, List<ProductPlan> allPlan) {
        if (!plan.getParentId().equals(ProductConst.PLAN_ROOT_ID)) {
            // 该项目所有计划
            if (allPlan == null) {
                allPlan = planRepository.getByProject(plan.getProjectCode());
            }
            Optional<ProductPlan> findParent = allPlan.stream().filter(a -> a.getId().equals(plan.getParentId())).findFirst();
            if (findParent.isPresent()) {
                ProductPlan parentPlan = findParent.get();
                List<ProductPlan> children = allPlan.stream().filter(a -> a.getParentId().equals(parentPlan.getId())).toList();
                if (CollUtil.isNotEmpty(children)) {
                    double sumCompletionRate = children.stream().collect(Collectors.summarizingDouble(p -> p.getCompletionRate() == null ? 0 : p.getCompletionRate())).getSum();
                    parentPlan.setCompletionRate(NumberUtil.div(sumCompletionRate, children.size(), 0));
                    log.info("更新父级完成率：{}", parentPlan);
                    planRepository.update(parentPlan);
                }
                // 向上计算父级完成率
                calculateCompletionRate(parentPlan, allPlan);
            }
        }
    }

    @Override
    public void afterUpdate(ProductPlan plan) {
        // 更新父节点进度（完成率）
        calculateCompletionRate(plan, null);
    }

    @Override
    public MultipartFile exportImpl(ProductPlan plan) {
        List<ProductPlan> entities = planRepository.getExportData(plan);
        List<ProductPlanExportR> results = new ArrayList<>();
        for (ProductPlan entity : entities) {
            results.add(toProductPlanExportR(entity));
        }
        String fileName = "生产计划" + System.currentTimeMillis();
        MockMultipartFile multipartFile;
        try {
            // 使用EasyExcel将文件写到流
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            EasyExcel.write(outStream, ProductPlanExportR.class).sheet(fileName).doWrite(results);
            // 将输出流转为 multipartFile 并上传
            multipartFile = new MockMultipartFile("file", fileName + ".xlsx", null, outStream.toByteArray());
            outStream.close();
        } catch (IOException e) {
            log.error(fileName + "导出错误:" + e.getMessage());
            throw new RuntimeException(fileName + "导出错误");
        }
        return multipartFile;
    }

    public ProductPlanExportR toProductPlanExportR(ProductPlan entity) {
        return ProductPlanExportR.builder().planStatus(entity.getPlanStatus())
                .serialNo(entity.getSerialNo())
                .projectCode(entity.getProjectCode())
                .name(entity.getName())
                .number(entity.getNumber())
                .level(ProductPlanLevel.getByLevel(entity.getLevel()).getName())
                .planStartTime(formatDate(entity.getLevel(), entity.getPlanStartTime()))
                .planEndTime(formatDate(entity.getLevel(), entity.getPlanEndTime()))
                .planPeriod(entity.getPlanPeriod())
                .actualStartTime(formatDate(entity.getLevel(), entity.getActualStartTime()))
                .actualEndTime(formatDate(entity.getLevel(), entity.getActualEndTime()))
                .actualPeriod(entity.getActualPeriod())
                .completionRate(entity.getCompletionRate())
                .build();
    }

    public String formatDate(Integer level, Date date) {
        if (date == null) {
            return "";
        }
        if (level.equals(ProductPlanLevel.PROJECT.getLevel()) || level.equals(ProductPlanLevel.EQUIPMENT.getLevel())) {
            return cn.hutool.core.date.DateUtil.format(date, "yyyy-MM-dd");
        } else {
            return cn.hutool.core.date.DateUtil.format(date, "yyyy-MM-dd HH");
        }
    }

    public void validateCreate(ProductPlan plan) {
        if (!plan.getLevel().equals(ProductPlanLevel.PROJECT.getLevel())) {
            validateParent(plan);
            List<ProductPlanDO> childrenList = planRepository.getChildrenList(plan.getParentId());
            if (CollUtil.isNotEmpty(childrenList)) {
                Optional<ProductPlanDO> findSameName = childrenList.stream().filter(c -> c.getName().equals(plan.getName())).findFirst();
                if (findSameName.isPresent()) {
                    throw new ServiceException("相同层级存在同名计划，请更换计划名称");
                }
            }
        }
    }

    public void validateParent(ProductPlan plan) {
        // 父id是否有效
        ProductPlan parentPlan = planRepository.getById(plan.getParentId());
        if (parentPlan == null) {
            throw new ServiceException(StrUtil.format("父级不存在，parentId:{}", plan.getParentId()));
        }
        if (!parentPlan.getSerialNo().equals(plan.getSerialNo())) {
            throw new ServiceException(StrUtil.format("与父级计划编号不一致，无法添加子级计划，父计划编号：{}，子计划编号：{}", parentPlan.getSerialNo(), plan.getSerialNo()));
        }
        if (!parentPlan.getProjectCode().equals(plan.getProjectCode())) {
            throw new ServiceException(StrUtil.format("与父级项目代码不一致，无法添加子级计划，父计划项目代码：{}，子计划项目代码：{}", parentPlan.getProjectCode(), plan.getProjectCode()));
        }
    }

    public void validateUpdate(ProductPlan plan) {
        if (plan.getId() != null) {
            ProductPlan oldEntity = planRepository.getById(plan.getId());
            if (oldEntity != null) {
                if (!oldEntity.getSerialNo().equals(plan.getSerialNo())) {
                    throw new ServiceException("计划编号不允许修改");
                }
                if (!oldEntity.getProjectCode().equals(plan.getProjectCode())) {
                    throw new ServiceException("项目代码不允许修改");
                }
                if (!Objects.equals(oldEntity.getLevel(), plan.getLevel())) {
                    throw new ServiceException("类型不允许修改");
                }

            }
        }
        if (!plan.getLevel().equals(ProductPlanLevel.PROJECT.getLevel())) {
            validateParent(plan);
            List<ProductPlanDO> childrenList = planRepository.getChildrenList(plan.getParentId());
            if (CollUtil.isNotEmpty(childrenList)) {
                Optional<ProductPlanDO> findSameName = childrenList.stream().filter(c -> c.getName().equals(plan.getName()) && !c.getId().equals(plan.getId())).findFirst();
                if (findSameName.isPresent()) {
                    throw new ServiceException("相同层级存在同名计划，请更换计划名称");
                }
            }
        }
    }

    public void validateGeneral(ProductPlan plan) {
        // 设备校验
        if (plan.getPlanType() == null) {
            throw new ServiceException("计划类型不能为空");
        }
        ProductPlanType.getByType(plan.getPlanType().toString());
        if (StrUtil.isBlank(plan.getSerialNo())) {
            throw new ServiceException("计划编号不能为空");
        }
        if (plan.getLevel() == null) {
            throw new ServiceException("类型不能为空");
        }
        ProductPlanLevel level = ProductPlanLevel.getByLevel(plan.getLevel());
        // 不是项目，父id必填
        if (!Objects.equals(ProductPlanLevel.PROJECT.getLevel(), plan.getLevel()) && plan.getParentId() == null) {
            throw new ServiceException(StrUtil.format("{}的parentId不能为空", level.getName()));
        }
        if (StrUtil.isBlank(plan.getProjectCode())) {
            throw new ServiceException("项目代码不能为空");
        }
        if (StrUtil.isBlank(plan.getName())) {
            throw new ServiceException("名称不能为空");
        }
        if (plan.getNumber() == null) {
            throw new ServiceException("数量不能为空");
        }
        if (plan.getPlanStartTime() == null) {
            throw new ServiceException("计划开始时间不能为空");
        }
        if (plan.getPlanEndTime() == null) {
            throw new ServiceException("计划结束时间不能为空");
        }
    }

    public void importProductPlan(List<ProductPlanImportVO> importVOs, ProductPlanType planType) {
        List<ProductPlan> productPlans = toProductPlansFromImport(importVOs, planType);
        List<ProductPlan> allowPlanLevelInNextLine = new ArrayList<>();
        for (ProductPlan importPlan : productPlans) {
            if (StrUtil.isEmpty(importPlan.getSerialNo())) {
                // 新增
                allowPlanLevelInNextLine = createAndSetParentId(importPlan, allowPlanLevelInNextLine);
            } else {
                ProductPlan updatePlan = planRepository.selectImportUpdatePlan(importPlan.getSerialNo(), importPlan.getLevel(), importPlan.getName());
                if (updatePlan == null) {
                    // 填了计划编号，但未找到，则视为新增
                    allowPlanLevelInNextLine = createAndSetParentId(importPlan, allowPlanLevelInNextLine);
                    continue;
                }
                // 更新
                importPlan.setId(updatePlan.getId());
                importPlan.setParentId(updatePlan.getParentId());
                // 不能通过导入来更新计划的状态
                importPlan.setPlanStatus(updatePlan.getPlanStatus());
                update(importPlan, updatePlan);
            }
        }
    }

    /**
     * 上一行计划：层级最大的
     *
     * @param allowPlanLevelInNextLine 下一行可用的计划层级
     * @return 最大层级计划
     */
    public ProductPlan getLastPlan(List<ProductPlan> allowPlanLevelInNextLine) {
        return allowPlanLevelInNextLine.stream().sorted(Comparator.comparing(ProductPlan::getLevel).reversed()).toList().get(0);
    }

    /**
     * 创建计划并设置父id
     *
     * @param importPlan               导入的计划
     * @param allowPlanLevelInNextLine 下一行可用的计划层级
     * @return 下一行可用的计划层级
     */
    private List<ProductPlan> createAndSetParentId(ProductPlan importPlan, List<ProductPlan> allowPlanLevelInNextLine) {
        // 第一行
        if (CollUtil.isEmpty(allowPlanLevelInNextLine)) {
            // 第一行必须是项目类型
            if (!importPlan.getLevel().equals(ProductPlanLevel.PROJECT.getLevel())) {
                log.info("格式不规范：未读取到项目类型。params:{}", importPlan);
                throw new ServiceException(StrUtil.format("格式不规范：未读取到项目类型"));
            }
            ProductPlan submit = submit(importPlan);
            allowPlanLevelInNextLine.add(submit);
            return allowPlanLevelInNextLine;
        }
        // 上一行
        ProductPlan lastPlan = getLastPlan(allowPlanLevelInNextLine);
        Integer lastLevel = lastPlan.getLevel();
        // 与上一行同级：父id和上一行相同
        if (importPlan.getLevel().equals(lastLevel)) {
            // 重置可用层级为当前导入行层级
            allowPlanLevelInNextLine.removeIf(s -> s.getLevel() >= importPlan.getLevel());
            importPlan.setParentId(lastPlan.getParentId());
            importPlan.setSerialNo(lastPlan.getSerialNo());
            ProductPlan submit = submit(importPlan);
            allowPlanLevelInNextLine.add(submit);
            return allowPlanLevelInNextLine;
        }
        // 上一行的下级：父id是上一行的id
        if (importPlan.getLevel() > lastLevel) {
            // 不允许跳层级的行
            if (importPlan.getLevel() - lastLevel > 1) {
                log.info("格式不规范：未读取到上级类型。params:{}", importPlan);
                throw new ServiceException(StrUtil.format("格式不规范：未读取到上级类型。名称：{}", importPlan.getName()));
            }
            importPlan.setParentId(lastPlan.getId());
            importPlan.setSerialNo(lastPlan.getSerialNo());
            ProductPlan submit = submit(importPlan);
            allowPlanLevelInNextLine.add(submit);
            return allowPlanLevelInNextLine;
        }
        // 可能是新的项目计划或不连续的层如：类型由组件-》设备
        Optional<ProductPlan> findSameLayer = allowPlanLevelInNextLine.stream().filter(s -> s.getLevel().equals(importPlan.getLevel())
                && s.getProjectCode().equals(importPlan.getProjectCode())).findFirst();
        // 存在同层
        if (findSameLayer.isPresent()) {
            // 重置可用层级为当前导入行层级
            allowPlanLevelInNextLine.removeIf(s -> s.getLevel() >= importPlan.getLevel());
            ProductPlan sameLayerPlan = findSameLayer.get();
            importPlan.setParentId(sameLayerPlan.getParentId());
            // 同层为项目，使用新计划编号
            if (!importPlan.getLevel().equals(ProductPlanLevel.PROJECT.getLevel())) {
                importPlan.setSerialNo(sameLayerPlan.getSerialNo());
            }
            ProductPlan submit = submit(importPlan);
            allowPlanLevelInNextLine.add(submit);
            return allowPlanLevelInNextLine;
        }
        // 新项目
        // 新项目的下一行可用层级重置
        allowPlanLevelInNextLine = new ArrayList<>();
        return createAndSetParentId(importPlan, allowPlanLevelInNextLine);
    }

    public List<ProductPlan> toProductPlansFromImport(List<ProductPlanImportVO> importVOs, ProductPlanType planType) {
        List<ProductPlan> productPlans = new ArrayList<>();
        for (ProductPlanImportVO importVO : importVOs) {
            ProductPlan plan = ProductPlan.builder().planType(planType.getType())
                    .planStatus(StrUtil.isBlank(importVO.getPlanStatus()) ? ProductPlanStatus.NOT_STARTED.getStatus() : ProductPlanStatus.getByStatus(importVO.getPlanStatus()).getStatus())
                    .serialNo(importVO.getSerialNo())
                    .projectCode(importVO.getProjectCode())
                    .level(ProductPlanLevel.getByName(importVO.getLevelName()).getLevel())
                    .name(importVO.getName())
                    .number(importVO.getNumber())
                    .planStartTime(importVO.getPlanStartTime())
                    .planEndTime(importVO.getPlanEndTime())
                    .actualStartTime(importVO.getActualStartTime())
                    .actualEndTime(importVO.getActualEndTime())
                    .completionRate(importVO.getCompletionRate()).build();
            if (StrUtil.isNotBlank(importVO.getReason()) || StrUtil.isNotBlank(importVO.getPlanChangeType())) {
                plan.setChangeReason(ProductPlanChangeReason.builder().changeReason(importVO.getReason())
                        .changeType(importVO.getPlanChangeType()).dept(importVO.getDept()).build());
            }
            productPlans.add(plan);
        }
        return productPlans;
    }

    @Override
    public void statusChange(ProductPlanStatusChangeCmd statusChangeCmd) {
        planRepository.statusChange(statusChangeCmd);
    }

}
