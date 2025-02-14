package com.greenstone.mes.asset.domain.repository.effect;

import lombok.Data;

/**
 * @author gu_renkai
 * @date 2023/2/3 14:28
 */
@Data
public class AssetTypeSaveEffect {

    private boolean typeCodeChanged;

    private boolean typeNameChanged;

    private boolean parentTypeChanged;

    public boolean isSomethingChanged() {
        return typeCodeChanged || typeNameChanged || parentTypeChanged;
    }

}
