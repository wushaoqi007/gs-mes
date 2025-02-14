package com.greenstone.mes.asset.infrastructure.mapper;

import com.greenstone.mes.asset.infrastructure.persistence.AssetDO;
import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import org.springframework.stereotype.Repository;

/**
 * @author gu_renkai
 * @date 2023/2/7 8:13
 */
@Repository
public interface AssetMapper extends EasyBaseMapper<AssetDO> {
}
