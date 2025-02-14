package com.greenstone.mes.bom.manager;

import com.greenstone.mes.bom.request.*;

public interface BomDetailManager {

    void delete(Long id);

    void update(BomDetailEditReq editRequest);

}
