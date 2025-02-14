package com.greenstone.mes.material.constant;

/**
 * 仓库
 */
public interface WareHouseConstant {

    /**
     * 仓库所属部门编号
     */
    interface Code {
        /**
         * 待质检仓库
         */
        String DZJ = "DJ";

        /**
         * 质检中仓库
         */
        String ZJZ = "ZJ";

        /**
         * 待表处仓库
         */
        String DBC = "BC";

        /**
         * 表处中仓库
         */
        String BCZ = "BCZ";

        /**
         * 待返工仓库
         */
        String DFG = "FG";

        /**
         * 返工中仓库
         */
        String FGZ = "FGZ";

        /**
         * 合格品仓库
         */
        String HGP = "HG";

        /**
         * 良品仓库
         */
        String LP = "LP";
    }

}
