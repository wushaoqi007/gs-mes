package com.greenstone.mes.oa.infrastructure.config;

/**
 * 考勤规则配置
 *
 * @author wushaoqi
 * @date 2022-04-18-10:43
 */
public class WxAttendanceOptionProperties {

    /**
     * 加班是否需要审批
     */
    public static final boolean overTimeNeedApproval = true;

    /**
     * 出差是否需要审批
     */
    public static final boolean businessTripNeedApproval = false;

    /**
     * 外出是否需要审批
     */
    public static final boolean outNeedApproval = false;

    /**
     * 夜班标准上班打卡时间（晚上8点距离0点的秒数）
     */
    public static final long nightWorkSec = 72000;

    /**
     * 夜班标准下班打卡时间（早上5点距离0点的秒数）
     */
    public static final long nightOffWorkSec = 18000;

}
