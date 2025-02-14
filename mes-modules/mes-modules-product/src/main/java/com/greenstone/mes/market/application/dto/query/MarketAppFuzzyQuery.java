package com.greenstone.mes.market.application.dto.query;

import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.Data;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/21 15:11
 */

@Data
public class MarketAppFuzzyQuery {

    private String key;

    private List<String> fields;

    private ProcessStatus state;

}
