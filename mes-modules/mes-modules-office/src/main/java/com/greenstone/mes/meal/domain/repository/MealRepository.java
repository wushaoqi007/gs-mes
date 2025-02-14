package com.greenstone.mes.meal.domain.repository;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.meal.domain.converter.MealConverter;
import com.greenstone.mes.meal.domain.entity.MealReport;
import com.greenstone.mes.meal.domain.entity.MealTicket;
import com.greenstone.mes.meal.infrastructure.constant.MealConst;
import com.greenstone.mes.meal.infrastructure.mapper.MealManageMapper;
import com.greenstone.mes.meal.infrastructure.mapper.MealReportMapper;
import com.greenstone.mes.meal.infrastructure.mapper.MealTicketMapper;
import com.greenstone.mes.meal.infrastructure.persistence.MealManageDo;
import com.greenstone.mes.meal.infrastructure.persistence.MealReportDo;
import com.greenstone.mes.meal.infrastructure.persistence.MealTicketDo;
import com.greenstone.mes.office.meal.dto.query.MealManageQuery;
import com.greenstone.mes.office.meal.dto.query.MealReportQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MealRepository {

    private final MealReportMapper mealReportMapper;
    private final MealTicketMapper mealTicketMapper;
    private final MealManageMapper mealManageMapper;
    private final MealConverter mealConverter;

    public List<MealReport> queryMealReports(MealReportQuery query) {
        if (query.getNotUsed() == null) {
            query.setNotUsed(false);
        }
        LambdaQueryWrapper<MealReportDo> queryWrapper = Wrappers.lambdaQuery(MealReportDo.class)
                .eq(StrUtil.isNotBlank(query.getReportBy()), MealReportDo::getReportBy, query.getReportBy())
                .eq(query.getMealType() != null, MealReportDo::getMealType, query.getMealType())
                .eq(query.getReportType() != null, MealReportDo::getReportType, query.getReportType())
                .eq(query.getDay() != null, MealReportDo::getDay, query.getDay())
                .ge(query.getStartDay() != null, MealReportDo::getDay, query.getStartDay())
                .le(query.getEndDay() != null, MealReportDo::getDay, query.getEndDay())
                .apply(query.getNotUsed(), "meal_num > used_num")
                .eq(query.getRevoked() != null, MealReportDo::getRevoked, query.getRevoked())
                .orderByDesc(MealReportDo::getDay)
                .orderByDesc(MealReportDo::getMealType);
        List<MealReportDo> mealReportDos = mealReportMapper.selectList(queryWrapper);
        return mealConverter.reportDos2Entities(mealReportDos);
    }

    public MealReport getReport(MealReport mealReport) {
        MealReportDo selectDo = mealConverter.entity2Do(mealReport);
        MealReportDo mealReportDo = mealReportMapper.getOneOnly(selectDo);
        return mealConverter.do2Entity(mealReportDo);
    }

    public List<MealManageDo> queryMealManages(MealManageQuery query) {
        LambdaQueryWrapper<MealManageDo> queryWrapper = Wrappers.lambdaQuery(MealManageDo.class)
                .eq(query.getMealType() != null, MealManageDo::getMealType, query.getMealType())
                .ge(query.getStartDay() != null, MealManageDo::getDay, query.getStartDay())
                .le(query.getEndDay() != null, MealManageDo::getDay, query.getEndDay())
                .orderByDesc(MealManageDo::getDay)
                .orderByDesc(MealManageDo::getMealType);
        return mealManageMapper.selectList(queryWrapper);
    }

    public MealManageDo getMealManage(Integer mealType, LocalDate day) {
        LambdaQueryWrapper<MealManageDo> queryWrapper = Wrappers.lambdaQuery(MealManageDo.class)
                .eq(mealType != null, MealManageDo::getMealType, mealType)
                .eq(day != null, MealManageDo::getDay, day);
        return mealManageMapper.selectOne(queryWrapper);
    }


    public List<MealTicket> getTickets(String reportId) {
        List<MealTicketDo> ticketDos = mealTicketMapper.list(MealTicketDo.builder().reportId(reportId).build());
        return mealConverter.ticketDos2Entities(ticketDos);
    }

    public List<MealTicket> getTickets(Integer mealType, Long reportUserId, LocalDate day) {
        MealTicketDo selectTicket = MealTicketDo.builder().day(day).reportById(reportUserId).mealType(mealType).build();
        List<MealTicketDo> ticketDos = mealTicketMapper.list(selectTicket);
        return mealConverter.ticketDos2Entities(ticketDos);
    }

    public List<MealTicket> getTicketsByUseTime(LocalDateTime useTimeStart, LocalDateTime useTimeEnd) {
        LambdaQueryWrapper<MealTicketDo> queryWrapper = Wrappers.lambdaQuery(MealTicketDo.class).ge(MealTicketDo::getUseTime, useTimeStart).le(MealTicketDo::getUseTime, useTimeEnd);
        List<MealTicketDo> ticketDos = mealTicketMapper.selectList(queryWrapper);
        return mealConverter.ticketDos2Entities(ticketDos);
    }

    public MealTicket getTicket(String code) {
        MealTicketDo selectTicket = MealTicketDo.builder().ticketCode(code).build();
        MealTicketDo ticketDo = mealTicketMapper.getOneOnly(selectTicket);
        return mealConverter.do2Entity(ticketDo);
    }

    /**
     * 添加报餐
     */
    public void addMealReport(MealReport mealReport) {
        // 保存报餐信息
        MealReportDo insertReport = mealConverter.entity2Do(mealReport);
        insertReport.setUpdateBy(null);
        insertReport.setUpdateById(null);
        insertReport.setUpdateTime(null);
        mealReportMapper.insert(insertReport);
        // 保存饭票信息
        List<MealTicketDo> mealTicketDos = mealConverter.entities2Dos(mealReport.getMealTickets());
        for (MealTicketDo mealTicketDo : mealTicketDos) {
            mealTicketDo.setReportId(insertReport.getId());
            mealTicketMapper.insert(mealTicketDo);
        }
        // 更新统计信息
        updateManage(mealConverter.do2Entity(insertReport));
    }

    /**
     * 撤回报餐
     */
    public void revokeMealReport(MealReport mealReport) {
        // 更新统计信息
        MealManageDo mealManageDo = updateManage(mealReport);
        // 如果报餐截止了，则保留撤回记录，删除餐券；如果没截止，则直接删除报餐记录，删除餐券
        if (mealManageDo.getStopped()) {
            mealReportMapper.updateById(MealReportDo.builder().id(mealReport.getId()).revoked(true).revokeType(mealReport.getReportType()).build());
        } else {
            mealReportMapper.deleteById(mealReport.getId());
        }
        mealTicketMapper.delete(MealTicketDo.builder().reportId(mealReport.getId()).build());
    }

    @Transactional
    public void useTicket(MealTicket ticket) {
        mealTicketMapper.updateById(MealTicketDo.builder().id(ticket.getId()).used(true).useTime(LocalDateTime.now()).build());
        LambdaUpdateWrapper<MealManageDo> updateManage = Wrappers.lambdaUpdate(MealManageDo.class)
                .eq(MealManageDo::getMealType, ticket.getMealType())
                .eq(MealManageDo::getDay, ticket.getDay())
                .setSql("meal_used_num = (meal_used_num + 1)");
        mealManageMapper.update(updateManage);
        LambdaUpdateWrapper<MealReportDo> updateReport = Wrappers.lambdaUpdate(MealReportDo.class)
                .eq(MealReportDo::getId, ticket.getReportId())
                .setSql("used_num = (used_num + 1)");
        mealReportMapper.update(updateReport);
    }

    public void updateTicketMediaId(MealTicket ticket) {
        LambdaUpdateWrapper<MealTicketDo> updateWrapper = Wrappers.lambdaUpdate(MealTicketDo.class).eq(MealTicketDo::getTicketCode, ticket.getTicketCode())
                .set(MealTicketDo::getWxMediaId, ticket.getWxMediaId());
        mealTicketMapper.update(updateWrapper);
    }

    public boolean isUniqueTicket(String ticketCode) {
        return !mealTicketMapper.exists(MealTicketDo.builder().ticketCode(ticketCode).build());
    }

    public boolean isReportExist(MealReport report) {
        MealReportDo reportDo = mealConverter.entity2Do(report);
        return mealReportMapper.exists(reportDo);
    }

    public void updateManage(MealManageDo mealManageDo) {
        LambdaQueryWrapper<MealManageDo> queryWrapper = Wrappers.lambdaQuery(MealManageDo.class).eq(MealManageDo::getMealType, mealManageDo.getMealType())
                .eq(MealManageDo::getDay, mealManageDo.getDay());
        mealManageMapper.update(mealManageDo, queryWrapper);
    }

    /**
     * 更新报餐管理统计数据
     * 报餐时，必须在报餐数据入库后更新统计数据
     * 撤回时，必须先更新统计数据再删除报餐数据
     *
     * @param existReport 当前存在的报餐数据
     */
    private synchronized MealManageDo updateManage(MealReport existReport) {
        MealManageDo selectManage = MealManageDo.builder().mealType(existReport.getMealType()).day(existReport.getDay()).build();
        MealManageDo existManage = mealManageMapper.getOneOnly(selectManage);
        if (existManage == null) {
            selectManage.setReportNum(0);
            selectManage.setMealNum(0);
            selectManage.setMealUsedNum(0);
            selectManage.setMealRevokeNum(0);
            mealManageMapper.insert(selectManage);
            existManage = selectManage;
        }
        switch (existReport.getReportType()) {
            // 更新报餐的数量
            case MealConst.ReportType.SELF_REPORT, MealConst.ReportType.ADMIN_REPORT,
                 MealConst.ReportType.ADDITIONAL_REPORT -> {
                // 一个员工可以有多个报餐记录，如果只有一条刚刚添加的记录，则增加一个报餐人数
                MealReportDo countReport = MealReportDo.builder().mealType(existReport.getMealType()).day(existReport.getDay()).reportById(existReport.getReportById()).build();
                if (mealReportMapper.selectCount(countReport) <= 1) {
                    existManage.setReportNum(existManage.getReportNum() + 1);
                }
                // 增加报餐数量
                if (existReport.getHaveMeal()) {
                    existManage.setMealNum(existManage.getMealNum() + existReport.getMealNum());
                }
            }
            // 报餐截止前更新撤回的数量，截止后不记录报餐总数
            case MealConst.ReportType.SELF_REVOKE, MealConst.ReportType.ADMIN_REVOKE,
                 MealConst.ReportType.SYS_REVOKE -> {
                if (existManage.getStopped()) {
                    log.info("Update manage data after report stopped.");
                    existManage.setMealRevokeNum(existManage.getMealRevokeNum() + existReport.getMealNum());
                } else {
                    log.info("Update manage data before report stopped.");
                    // 一个员工可以有多个报餐记录，如果只有一条需要删除的记录，则减少一个报餐人数
                    MealReportDo countReport = MealReportDo.builder().mealType(existReport.getMealType()).day(existReport.getDay()).reportById(existReport.getReportById()).build();
                    if (mealReportMapper.selectCount(countReport) <= 1) {
                        existManage.setReportNum(existManage.getReportNum() - 1);
                    }
                    if (existReport.getHaveMeal()) {
                        existManage.setMealNum(existManage.getMealNum() - existReport.getMealNum());
                    }
                }
            }
        }
        mealManageMapper.updateById(existManage);
        return existManage;
    }

    public boolean stopReport(Integer mealType, LocalDate day) {
        MealManageDo existManage = mealManageMapper.getOneOnly(MealManageDo.builder().mealType(mealType).day(day).build());
        if (existManage == null) {
            return false;
        }
        LambdaUpdateWrapper<MealManageDo> updateWrapper = Wrappers.lambdaUpdate(MealManageDo.class)
                .set(MealManageDo::getStopped, true)
                .set(MealManageDo::getStopTime, LocalDateTime.now())
                .eq(MealManageDo::getMealType, mealType)
                .eq(MealManageDo::getDay, day);
        return mealManageMapper.update(updateWrapper) > 0;
    }

    public boolean stopAdditionalReport(Integer mealType, LocalDate day) {
        MealManageDo existManage = mealManageMapper.getOneOnly(MealManageDo.builder().mealType(mealType).day(day).build());
        if (existManage == null) {
            return false;
        }
        LambdaUpdateWrapper<MealManageDo> updateWrapper = Wrappers.lambdaUpdate(MealManageDo.class)
                .set(MealManageDo::getAdditionalReportStopped, true)
                .set(MealManageDo::getAdditionalReportStopTime, LocalDateTime.now())
                .eq(MealManageDo::getMealType, mealType)
                .eq(MealManageDo::getDay, day);
        return mealManageMapper.update(updateWrapper) > 0;
    }

    public boolean isStopped(Integer mealType, LocalDate day) {
        MealManageDo manage = mealManageMapper.getOneOnly(MealManageDo.builder().mealType(mealType).day(day).build());
        return manage != null && manage.getStopped();
    }

    public boolean additionalReportIsStopped(Integer mealType, LocalDate day) {
        MealManageDo manage = mealManageMapper.getOneOnly(MealManageDo.builder().mealType(mealType).day(day).build());
        return manage != null && manage.getAdditionalReportStopped();
    }

}
