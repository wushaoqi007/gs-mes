package com.greenstone.mes.system.domain.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.greenstone.mes.common.core.utils.TreeUtils;
import com.greenstone.mes.common.security.utils.DictUtils;
import com.greenstone.mes.system.api.domain.SysDictData;
import com.greenstone.mes.system.domain.service.ISysDictDataService;
import com.greenstone.mes.system.infrastructure.mapper.SysDictDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典 业务层处理
 * 
 * @author ruoyi
 */
@Service
public class SysDictDataServiceImpl implements ISysDictDataService
{
    @Autowired
    private SysDictDataMapper dictDataMapper;

    /**
     * 根据条件分页查询字典数据
     * 
     * @param dictData 字典数据信息
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData)
    {
        List<SysDictData> sysDictData = dictDataMapper.selectDictDataList(dictData);
        return TreeUtils.toTree(sysDictData);
    }

    /**
     * 根据字典类型和字典键值查询字典数据信息
     * 
     * @param dictType 字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    @Override
    public String selectDictLabel(String dictType, String dictValue)
    {
        return dictDataMapper.selectDictLabel(dictType, dictValue);
    }

    /**
     * 根据字典数据ID查询信息
     * 
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    @Override
    public SysDictData selectDictDataById(Long dictCode)
    {
        return dictDataMapper.selectDictDataById(dictCode);
    }

    /**
     * 批量删除字典数据信息
     * 
     * @param dictCodes 需要删除的字典数据ID
     * @return 结果
     */
    @Override
    public void deleteDictDataByIds(Long[] dictCodes)
    {
        for (Long dictCode : dictCodes)
        {
            SysDictData data = selectDictDataById(dictCode);
            // 删除子级
            deleteChildrenDictData(data.getDictCode());
            dictDataMapper.deleteDictDataById(dictCode);
            List<SysDictData> dictDatas = TreeUtils.toTree(dictDataMapper.selectDictDataByType(data.getDictType()));
            DictUtils.setDictCache(data.getDictType(), dictDatas);
        }
    }

    public void deleteChildrenDictData(Long dictCode){
        List<SysDictData> deleteDataList = dictDataMapper.selectChildrenDictDataByDictCode(dictCode);
        if(CollUtil.isNotEmpty(deleteDataList)){
            for (SysDictData deleteData : deleteDataList) {
                // 递归删除子级
                deleteChildrenDictData(deleteData.getDictCode());
                dictDataMapper.deleteDictDataById(deleteData.getDictCode());
            }
        }

    }

    /**
     * 新增保存字典数据信息
     * 
     * @param data 字典数据信息
     * @return 结果
     */
    @Override
    public int insertDictData(SysDictData data)
    {
        int row = dictDataMapper.insertDictData(data);
        if (row > 0)
        {
            List<SysDictData> dictDatas = TreeUtils.toTree(dictDataMapper.selectDictDataByType(data.getDictType()));
            DictUtils.setDictCache(data.getDictType(), dictDatas);
        }
        return row;
    }

    /**
     * 修改保存字典数据信息
     * 
     * @param data 字典数据信息
     * @return 结果
     */
    @Override
    public int updateDictData(SysDictData data)
    {
        int row = dictDataMapper.updateDictData(data);
        if (row > 0)
        {
            List<SysDictData> dictDatas = TreeUtils.toTree(dictDataMapper.selectDictDataByType(data.getDictType()));
            DictUtils.setDictCache(data.getDictType(), dictDatas);
        }
        return row;
    }

    @Override
    public JSONArray selectDictDataOfLevel(SysDictData dict) {
        List<SysDictData> sysDictData = selectDictDataList(dict);
        // 层级格式示例：A:A1、A2   B:B1、B2(目前支持2级)
        // 找到父节点
        List<SysDictData> parentData = sysDictData.stream().filter(d -> d.getDictValue().length() == 1).collect(Collectors.toList());
        // 找到一级子节点
        List<SysDictData> childData = sysDictData.stream().filter(d -> d.getDictValue().length() >1).collect(Collectors.toList());
        JSONArray dictDataArray = new JSONArray();
        for (SysDictData parentDatum : parentData) {
            JSONObject parentJson = new JSONObject();
            dictDataArray.add(parentJson);
            parentJson.put("value",parentDatum.getDictValue());
            parentJson.put("label",parentDatum.getDictLabel());
            JSONArray childArray = new JSONArray();
            parentJson.put("children",childArray);
            for (SysDictData childDatum : childData) {
                if(childDatum.getDictValue().contains(parentDatum.getDictValue())){
                    JSONObject childJson = new JSONObject();
                    childJson.put("value",childDatum.getDictValue());
                    childJson.put("label",childDatum.getDictLabel());
                    childArray.add(childJson);
                }
            }
        }
        return dictDataArray;
    }
}
