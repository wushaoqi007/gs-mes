package com.greenstone.mes.material.constant;

/**
 * 工序类型
 */
public interface WorkProcedureConstant {

    /**
     * 工序类型
     */
    interface Type {
        /**
         * 待质检
         */
        String DSJ = "0";
        /**
         * 待质检
         */
        String DZJ = "1";

        /**
         * 质检中
         */
        String ZJZ = "2";

        /**
         * 待表处
         */
        String DBC = "3";

        /**
         * 表处中
         */
        String BCZ = "4";

        /**
         * 待返工
         */
        String DFG = "5";

        /**
         * 返工中
         */
        String FGZ = "6";

        /**
         * 合格品
         */
        String HGP = "7";

        /**
         * 良品
         */
        String LP = "8";

        /**
         * 未知
         */
        String WZ = "9";
    }

}
