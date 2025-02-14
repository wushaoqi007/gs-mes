package com.greenstone.mes.bom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.bom.domain.BomDetail;
import com.greenstone.mes.bom.mapper.BomDetailMapper;
import com.greenstone.mes.bom.service.BomDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * BOM明细Service业务层处理
 *
 * @author gu_renkai
 * @date 2022-01-25
 */
@Service
public class BomDetailServiceImpl extends ServiceImpl<BomDetailMapper, BomDetail> implements BomDetailService {

    @Autowired
    private BomDetailMapper bomDetailMapper;


    @Override
    public void saveOrUpdateDetail(BomDetail bomDetail) {
        BomDetail detailSelectEntity = BomDetail.builder().bomId(bomDetail.getBomId()).materialId(bomDetail.getMaterialId()).build();
        BomDetail existDetail = this.getOneOnly(detailSelectEntity);
        if (existDetail == null) {
            this.save(bomDetail);
        } else {
            BomDetail detailUpdateEntity = BomDetail.builder().id(existDetail.getId()).materialNumber(existDetail.getMaterialNumber() + bomDetail.getMaterialNumber()).build();
            this.updateById(detailUpdateEntity);
        }
    }

    /**
     * 查询BOM明细
     *
     * @param id BOM明细主键
     * @return BOM明细
     */
    @Override
    public BomDetail selectBomDetailById(Long id) {
        return bomDetailMapper.selectBomDetailById(id);
    }

    /**
     * 查询BOM明细列表
     *
     * @param bomDetail BOM明细
     * @return BOM明细
     */
    @Override
    public List<BomDetail> selectBomDetailList(BomDetail bomDetail) {
        return bomDetailMapper.selectBomDetailList(bomDetail);
    }

    /**
     * 新增BOM明细
     *
     * @param bomDetail BOM明细
     * @return 结果
     */
    @Override
    public int insertBomDetail(BomDetail bomDetail) {
        return bomDetailMapper.insertBomDetail(bomDetail);
    }

    /**
     * 修改BOM明细
     *
     * @param bomDetail BOM明细
     * @return 结果
     */
    @Override
    public int updateBomDetail(BomDetail bomDetail) {
        return bomDetailMapper.updateBomDetail(bomDetail);
    }

    /**
     * 批量删除BOM明细
     *
     * @param ids 需要删除的BOM明细主键
     * @return 结果
     */
    @Override
    public int deleteBomDetailByIds(Long[] ids) {
        return bomDetailMapper.deleteBomDetailByIds(ids);
    }

    /**
     * 删除BOM明细信息
     *
     * @param id BOM明细主键
     * @return 结果
     */
    @Override
    public int deleteBomDetailById(Long id) {
        return bomDetailMapper.deleteBomDetailById(id);
    }
}