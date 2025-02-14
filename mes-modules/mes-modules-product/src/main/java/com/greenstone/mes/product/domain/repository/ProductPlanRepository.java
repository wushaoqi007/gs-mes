package com.greenstone.mes.product.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.machine.application.helper.MachineHelper;
import com.greenstone.mes.product.application.dto.cmd.ProductPlanStatusChangeCmd;
import com.greenstone.mes.product.domain.converter.ProductPlanConverter;
import com.greenstone.mes.product.domain.entity.ProductPlan;
import com.greenstone.mes.product.infrastructure.constant.ProductConst;
import com.greenstone.mes.product.infrastructure.enums.ProductPlanLevel;
import com.greenstone.mes.product.infrastructure.enums.ProductPlanStatus;
import com.greenstone.mes.product.infrastructure.enums.ProductPlanType;
import com.greenstone.mes.product.infrastructure.mapper.ProductPlanMapper;
import com.greenstone.mes.product.infrastructure.persistence.ProductPlanDO;
import com.greenstone.mes.table.core.AbstractTableRepository;
import com.greenstone.mes.table.infrastructure.constant.TableConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class ProductPlanRepository extends AbstractTableRepository<ProductPlan, ProductPlanDO, ProductPlanMapper> {

    private final ProductPlanConverter converter;

    public ProductPlanRepository(ProductPlanMapper mapper, ProductPlanConverter converter) {
        super(mapper);
        this.converter = converter;
    }

    public void statusChange(ProductPlanStatusChangeCmd statusChangeCmd) {
        for (Long id : statusChangeCmd.getIds()) {
            ProductPlanDO planDO = mapper.selectById(id);
            planDO.setPlanStatus(statusChangeCmd.getPlanStatus().getStatus());
            mapper.updateById(planDO);
            // 更新项目计划时状态时，同时更新顶层项目状态：项目状态显示优先级：进行中 未开始 暂停 已完成 取消
            updateProjectStatus(planDO);
        }
    }

    public void updateProjectStatus(ProductPlanDO planDO) {
        if (planDO.getLevel().equals(ProductPlanLevel.PROJECT.getLevel())) {
            List<ProductPlanDO> children = mapper.list(ProductPlanDO.builder().parentId(planDO.getParentId()).dataStatus(TableConst.DataStatus.EFFECTIVE).build());
            for (ProductPlanStatus productPlanStatus : ProductPlanStatus.priorityList()) {
                Optional<ProductPlanDO> find = children.stream().filter(c -> c.getPlanStatus().equals(productPlanStatus.getStatus())).findFirst();
                if (find.isPresent()) {
                    LambdaUpdateWrapper<ProductPlanDO> updateWrapper = Wrappers.lambdaUpdate(ProductPlanDO.class)
                            .set(ProductPlanDO::getPlanStatus, productPlanStatus.getStatus())
                            .eq(ProductPlanDO::getId, planDO.getParentId());
                    mapper.update(updateWrapper);
                    break;
                }
            }
        }
    }

    public ProductPlan selectImportUpdatePlan(String serialNo, Integer level, String name) {
        ProductPlanDO productPlanDO = mapper.getOneOnly(ProductPlanDO.builder().serialNo(serialNo).level(level).name(name).dataStatus(TableConst.DataStatus.EFFECTIVE).build());
        return converter.toProductPlan(productPlanDO);
    }

    public List<ProductPlan> getByProject(String projectCode) {
        List<ProductPlanDO> productPlanDOs = mapper.list(ProductPlanDO.builder().projectCode(projectCode).dataStatus(TableConst.DataStatus.EFFECTIVE).build());
        return converter.toProductPlans(productPlanDOs);
    }

    public Boolean isExist(String serialNo) {
        return mapper.exists(ProductPlanDO.builder().serialNo(serialNo).dataStatus(TableConst.DataStatus.EFFECTIVE).build());
    }

    @Override
    public ProductPlan getEntity(Long id) {
        ProductPlanDO planDO = mapper.selectById(id);
        ProductPlan plan = converter.toProductPlan(planDO);
        // 顶层计划需要补全信息
        if (plan != null && ProductPlanLevel.TOP.getLevel().equals(plan.getLevel())) {
            List<ProductPlanDO> myChildren = mapper.list(ProductPlanDO.builder().parentId(plan.getId()).dataStatus(TableConst.DataStatus.EFFECTIVE).build());
            completion(myChildren, plan);
        }
        assert plan != null;
        setPlanPeriod(plan);
        return plan;
    }

    public ProductPlan getById(Long id) {
        return converter.toProductPlan(mapper.selectById(id));
    }

    public List<ProductPlanDO> getChildrenList(Long parentId) {
        return mapper.list(ProductPlanDO.builder().parentId(parentId).dataStatus(TableConst.DataStatus.EFFECTIVE).build());
    }

    @Override
    public List<ProductPlan> getEntities(ProductPlan productPlan) {
        LambdaQueryWrapper<ProductPlanDO> lambdaQuery = Wrappers.lambdaQuery(ProductPlanDO.class);
        // 懒加载
        lambdaQuery.eq(productPlan.getParentId() != null, ProductPlanDO::getParentId, productPlan.getParentId());
        lambdaQuery.eq(productPlan.getLevel() != null, ProductPlanDO::getLevel, productPlan.getLevel());

        lambdaQuery.eq(productPlan.getPlanType() != null, ProductPlanDO::getPlanType, productPlan.getPlanType());
        lambdaQuery.eq(StrUtil.isNotEmpty(productPlan.getProjectCode()), ProductPlanDO::getProjectCode, productPlan.getProjectCode());
        // 提交后是生效状态，删除是废弃，只查询提交的
        lambdaQuery.eq(ProductPlanDO::getDataStatus, TableConst.DataStatus.EFFECTIVE);
        if (CollUtil.isNotEmpty(productPlan.getParams())) {
            log.info("getEntities params:{}", productPlan.getParams());
            Map<String, Object> params = productPlan.getParams();
            List<Object> statusList = MachineHelper.ObjectToList(params.get("queryStatus"));
            lambdaQuery.in(CollUtil.isNotEmpty(statusList), ProductPlanDO::getPlanStatus, statusList);
            if (params.get("startTime") != null && params.get("endTime") != null) {
                String startTime = params.get("startTime").toString();
                String endTime = params.get("endTime").toString();
                lambdaQuery.and(w -> w.or(w1 -> w1.ge(ProductPlanDO::getPlanStartTime, startTime).le(ProductPlanDO::getPlanStartTime, endTime))
                        .or(w1 -> w1.ge(ProductPlanDO::getPlanEndTime, startTime).le(ProductPlanDO::getPlanEndTime, endTime))
                        .or(w1 -> w1.le(ProductPlanDO::getPlanStartTime, startTime).ge(ProductPlanDO::getPlanEndTime, endTime)));
            }
            List<Object> ids = MachineHelper.ObjectToList(params.get("ids"));
            lambdaQuery.in(CollUtil.isNotEmpty(ids), ProductPlanDO::getId, ids);
        }
        lambdaQuery.orderByDesc(ProductPlanDO::getCreateTime);
        List<ProductPlan> productPlans = converter.toProductPlans(mapper.selectByDataScopeLambda(lambdaQuery));
        // 顶层计划需要补全信息
        if (CollUtil.isNotEmpty(productPlans) && ProductPlanLevel.TOP.getLevel().equals(productPlan.getLevel())) {
            List<Long> topPlanIds = productPlans.stream().map(ProductPlan::getId).toList();
            lambdaQuery.clear();
            lambdaQuery.in(ProductPlanDO::getParentId, topPlanIds);
            lambdaQuery.eq(ProductPlanDO::getDataStatus, TableConst.DataStatus.EFFECTIVE);
            List<ProductPlanDO> topPlanChildren = mapper.selectList(lambdaQuery);
            productPlans.forEach(topPlan -> {
                List<ProductPlanDO> myChildren = topPlanChildren.stream().filter(c -> c.getParentId().equals(topPlan.getId())).toList();
                completion(myChildren, topPlan);
            });
        }
        for (ProductPlan plan : productPlans) {
            setPlanPeriod(plan);
        }
        return productPlans;
    }

    public void completion(List<ProductPlanDO> myChildren, ProductPlan plan) {
        if (CollUtil.isNotEmpty(myChildren)) {
            Optional<ProductPlanDO> minPlanStartTime = myChildren.stream().filter(a -> a.getPlanStartTime() != null).min(Comparator.comparing(ProductPlanDO::getPlanStartTime));
            Optional<ProductPlanDO> maxPlanEndTime = myChildren.stream().filter(a -> a.getPlanEndTime() != null).max(Comparator.comparing(ProductPlanDO::getPlanEndTime));
            Optional<ProductPlanDO> minActualStartTime = myChildren.stream().filter(a -> a.getActualStartTime() != null).min(Comparator.comparing(ProductPlanDO::getActualStartTime));
            Optional<ProductPlanDO> maxActualEndTime = myChildren.stream().filter(a -> a.getActualEndTime() != null).max(Comparator.comparing(ProductPlanDO::getActualEndTime));
            minPlanStartTime.ifPresent(productPlanDO -> plan.setPlanStartTime(productPlanDO.getPlanStartTime()));
            maxPlanEndTime.ifPresent(productPlanDO -> plan.setPlanEndTime(productPlanDO.getPlanEndTime()));
            minActualStartTime.ifPresent(productPlanDO -> plan.setActualStartTime(productPlanDO.getActualStartTime()));
            maxActualEndTime.ifPresent(productPlanDO -> plan.setActualEndTime(productPlanDO.getActualEndTime()));
        }
    }

    public List<ProductPlan> getExportData(ProductPlan productPlan) {
        List<ProductPlan> results = new ArrayList<>();

        if (CollUtil.isNotEmpty(productPlan.getParams())) {
            Map<String, Object> params = productPlan.getParams();
            List<Object> ids = MachineHelper.ObjectToList(params.get("ids"));
            LambdaQueryWrapper<ProductPlanDO> lambdaQuery = Wrappers.lambdaQuery(ProductPlanDO.class);
            lambdaQuery.in(CollUtil.isNotEmpty(ids), ProductPlanDO::getId, ids);
            lambdaQuery.eq(productPlan.getPlanType() != null, ProductPlanDO::getPlanType, productPlan.getPlanType());
            // 提交后是生效状态，删除是废弃，只查询提交的
            lambdaQuery.eq(ProductPlanDO::getDataStatus, TableConst.DataStatus.EFFECTIVE);
            // 勾选的计划
            List<ProductPlanDO> productPlanDOS = mapper.selectList(lambdaQuery);
            List<String> serialNos = productPlanDOS.stream().map(ProductPlanDO::getSerialNo).toList();
            lambdaQuery.clear();
            lambdaQuery.in(ProductPlanDO::getSerialNo, serialNos);
            lambdaQuery.eq(ProductPlanDO::getDataStatus, TableConst.DataStatus.EFFECTIVE);
            // 所有查询的计划（包含所有子计划）
            List<ProductPlanDO> allPlansInSerialNos = mapper.selectList(lambdaQuery);
            // 按勾选计划排序，附带所有子级
            for (Object id : ids) {
                Optional<ProductPlanDO> find = allPlansInSerialNos.stream().filter(a -> a.getId().equals(Long.parseLong(id.toString()))).findFirst();
                if (find.isPresent()) {
                    ProductPlanDO planDO = find.get();
                    ProductPlan plan = converter.toProductPlan(planDO);
                    setPlanPeriod(plan);
                    results.add(plan);
                    // 有子级
                    setChildren(results, allPlansInSerialNos, planDO);
                }
            }
        }
        return results;
    }

    public void setChildren(List<ProductPlan> results, List<ProductPlanDO> allPlansInSerialNos, ProductPlanDO plan) {
        // 有子级
        List<ProductPlanDO> findChildren = allPlansInSerialNos.stream().filter(a -> a.getParentId().equals(plan.getId())).toList();
        if (CollUtil.isNotEmpty(findChildren)) {
            for (ProductPlanDO findChild : findChildren) {
                ProductPlan childPlan = converter.toProductPlan(findChild);
                setPlanPeriod(childPlan);
                results.add(childPlan);
                setChildren(results, allPlansInSerialNos, findChild);
            }
        }
    }

    @Override
    public List<ProductPlan> getDrafts() {
        QueryWrapper<ProductPlanDO> query = Wrappers.query();
        query.eq("data_status", TableConst.DataStatus.DRAFT);
        query.eq("create_by", SecurityUtils.getUserId());
        return converter.toProductPlans(mapper.selectList(query));
    }

    @Override
    public ProductPlan insert(ProductPlan productPlan) {
        ProductPlanDO productPlanDO = converter.toProductPlanDO(productPlan);
        mapper.insert(productPlanDO);
        updateProjectStatus(productPlanDO);
        productPlan.setId(productPlanDO.getId());
        return productPlan;
    }

    @Override
    public ProductPlan update(ProductPlan productPlan) {
        ProductPlanDO productPlanDO = converter.toProductPlanDO(productPlan);
        mapper.updateById(productPlanDO);
        updateProjectStatus(productPlanDO);
        return productPlan;
    }

    @Override
    public void delete(Long id) {
        deleteWithChildren(id);
        ProductPlanDO planDO = mapper.selectById(id);
        // 删除的项目计划层级，如果项目没有其他计划，则删除项目
        if (planDO.getLevel().equals(ProductPlanLevel.PROJECT.getLevel())) {
            boolean existsOtherEff = mapper.exists(ProductPlanDO.builder().level(ProductPlanLevel.PROJECT.getLevel()).projectCode(planDO.getProjectCode()).dataStatus(TableConst.DataStatus.EFFECTIVE).build());
            if (!existsOtherEff) {
                mapper.deleteById(planDO.getParentId());
            }
        }

    }

    public void deleteWithChildren(Long id) {
        // 有子级一起删除
        List<ProductPlanDO> childrenList = getChildrenList(id);
        if (CollUtil.isNotEmpty(childrenList)) {
            for (ProductPlanDO child : childrenList) {
                deleteWithChildren(child.getId());
                mapper.deleteById(child.getId());
            }
        }
        mapper.deleteById(id);
    }

    @Override
    public void waste(Long id) {
        wasteWithChildren(id);
        ProductPlanDO planDO = mapper.selectById(id);
        // 废弃的项目计划层级，如果项目没有其他计划，则废弃项目
        if (planDO.getLevel().equals(ProductPlanLevel.PROJECT.getLevel())) {
            boolean existsOtherEff = mapper.exists(ProductPlanDO.builder().level(ProductPlanLevel.PROJECT.getLevel()).projectCode(planDO.getProjectCode()).dataStatus(TableConst.DataStatus.EFFECTIVE).build());
            if (!existsOtherEff) {
                LambdaUpdateWrapper<ProductPlanDO> updateWrapper = Wrappers.lambdaUpdate(ProductPlanDO.class)
                        .set(ProductPlanDO::getDataStatus, TableConst.DataStatus.WASTE)
                        .eq(ProductPlanDO::getId, planDO.getParentId());
                mapper.update(updateWrapper);
            }
        }
    }

    public void wasteWithChildren(Long id) {
        // 有子级一起废弃
        List<ProductPlanDO> childrenList = getChildrenList(id);
        if (CollUtil.isNotEmpty(childrenList)) {
            for (ProductPlanDO child : childrenList) {
                wasteWithChildren(child.getId());
            }
        }
        super.waste(id);
    }

    public void setPlanPeriod(ProductPlan plan) {
        if (plan.getLevel() <= ProductPlanLevel.EQUIPMENT.getLevel()) {
            plan.setPlanPeriod(getDayPeriod(plan.getPlanStartTime(), plan.getPlanEndTime()));
            plan.setActualPeriod(getDayPeriod(plan.getActualStartTime(), plan.getActualEndTime()));
        }
        if (plan.getLevel() >= ProductPlanLevel.COMPONENT_LEVEL_ONE.getLevel()) {
            plan.setPlanPeriod(getHourPeriod(plan.getPlanStartTime(), plan.getPlanEndTime()));
            plan.setActualPeriod(getHourPeriod(plan.getActualStartTime(), plan.getActualEndTime()));
        }
    }

    public String getDayPeriod(Date start, Date end) {
        if (start == null || end == null) {
            return null;
        }
        return DateUtil.between(start, end, DateUnit.DAY) + 1 + "天";
    }

    public String getHourPeriod(Date start, Date end) {
        if (start == null || end == null) {
            return null;
        }
        return DateUtil.between(start, end, DateUnit.HOUR) + "小时";
    }

    public ProductPlan selectTopPlan(ProductPlan plan) {
        ProductPlanDO queryPlan = ProductPlanDO.builder().parentId(ProductConst.PLAN_ROOT_ID).projectCode(plan.getProjectCode()).dataStatus(TableConst.DataStatus.EFFECTIVE).build();
        ProductPlanDO topPlan = mapper.getOneOnly(queryPlan);
        if (topPlan == null) {
            topPlan = ProductPlanDO.builder().projectCode(plan.getProjectCode()).parentId(ProductConst.PLAN_ROOT_ID).level(ProductPlanLevel.TOP.getLevel())
                    .planType(ProductPlanType.ASSEMBLY.getType()).planStatus(ProductPlanStatus.NOT_STARTED.getStatus()).dataStatus(TableConst.DataStatus.EFFECTIVE).build();
            mapper.insert(topPlan);
        }
        return converter.toProductPlan(topPlan);
    }
}
