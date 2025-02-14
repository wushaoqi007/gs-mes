package com.greenstone.mes.meal.infrastructure.constant;

public interface MealConst {

    interface MealType {

        int LUNCH = 1;

        int DINNER = 2;

    }

    interface ReportType {
        /**
         * 自主报餐
         */
        int SELF_REPORT = 1;
        /**
         * 管理员报餐
         */
        int ADMIN_REPORT = 2;
        /**
         * 自主撤回
         */
        int SELF_REVOKE = 3;
        /**
         * 管理员撤回
         */
        int ADMIN_REVOKE = 4;
        /**
         * 系统撤回
         */
        int SYS_REVOKE = 5;
        /**
         * 补餐
         */
        int ADDITIONAL_REPORT = 6;
    }

}
