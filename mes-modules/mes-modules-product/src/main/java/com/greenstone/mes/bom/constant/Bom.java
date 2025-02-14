package com.greenstone.mes.bom.constant;

public interface Bom {

    interface AddStrategy {
        int DO_NOTHING = 0;
        int UPDATE = 1;
        int THROW_ERROR = 2;
    }

}
