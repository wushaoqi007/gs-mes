package com.greenstone.mes.oa.constant;

public interface DesignProjectConst {

    interface Type {
        int DESIGN = 0;
        int PLAN = 1;
    }

    interface Phase {
        int CUSTOMER = 0;
        int PLAN = 1;
        int ACTUAL = 2;
    }

    interface Status {
        int DELAY = 0;
        int ON_TIME = 1;
        int WARN = 2;
    }

}
