package com.greenstone.mes.asset.application.dto.cqe.query;

import com.greenstone.mes.asset.infrastructure.enums.AssetBillType;
import lombok.Data;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/9 15:51
 */
@Data
public class AssetFuzzyQuery {

    private AssetBillType billType;

    private String keyWord;

    private List<String> fields;

}
