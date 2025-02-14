package com.greenstone.mes.meal.application.service;

import com.greenstone.mes.meal.domain.entity.MealTicket;
import com.greenstone.mes.office.meal.dto.cmd.*;
import com.greenstone.mes.office.meal.dto.query.MealManageQuery;
import com.greenstone.mes.office.meal.dto.query.MealReportQuery;
import com.greenstone.mes.office.meal.dto.query.TicketUseStatQuery;
import com.greenstone.mes.office.meal.dto.result.MealManageResult;
import com.greenstone.mes.office.meal.dto.result.MealReportResult;
import com.greenstone.mes.office.meal.dto.result.TicketUseResult;
import com.greenstone.mes.office.meal.dto.result.TicketUseStatResult;
import com.greenstone.mes.wxcp.domain.types.CpId;

import java.util.List;

public interface MealService {

    List<MealReportResult> queryReposts(MealReportQuery query);

    List<MealManageResult> queryManages(MealManageQuery query);

    void adminReport(AdminMealReportCmd reportCmd);

    void adminRevoke(MealRevokeCmd revokeCmd);

    void selfReport(MealReportCmd mealReportCmd);

    void selfRevoke(MealRevokeCmd revokeCmd);

    void sysRevoke(MealApplyCancelRevokeCmd revokeCmd);

    void sendMealTicket(CpId cpId, String wxUserId);

    void sendMealTicketImg(CpId cpId, Integer appId, String wxUserId, MealTicket mealTicket, Integer num);

    void sendMealReportCard(Integer mealType, CpId wxCpId, String wxUserId);

    void sendFrdReportCard(Integer mealType, CpId wxCpId, String wxUserId);

    TicketUseResult useTicket(String ticketCode);

    boolean stopReport(StopReportCmd stopReportCmd);

    void recalculate(ReCalcCmd reCalcCmd);

    boolean stopReport();

    boolean stopAdditionalReport();

    void lunchReportRemind();

    void sendReportStatData();

    List<TicketUseStatResult> queryTicketUseStat(TicketUseStatQuery useStatQuery);
}
