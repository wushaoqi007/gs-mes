package com.greenstone.mes.common.utils.excel;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.greenstone.mes.common.core.utils.SpringUtils;
import com.greenstone.mes.system.api.RemoteDictService;
import com.greenstone.mes.system.api.domain.SysDictData;

import java.util.List;

public class ExcelDictConvert implements Converter<String> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return String.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * 这里是读的时候会调用 不用管
     *
     * @return
     */
    @Override
    public String convertToJavaData(ReadConverterContext<?> context) {
        return context.getReadCellData().getStringValue();
    }

    /**
     * 这里是写的时候会调用 不用管
     *
     * @return
     */
    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<String> context) {
        String value = String.valueOf(context.getValue());
        if (StrUtil.isBlank(value)) {
            return new WriteCellData<>(value);
        }
        ExcelDictType annotation = context.getContentProperty().getField().getAnnotation(ExcelDictType.class);
        if (annotation != null) {
            String dictType = annotation.value();
            if (StrUtil.isNotEmpty(dictType)) {
                RemoteDictService dictService = SpringUtils.getBean(RemoteDictService.class);
                List<SysDictData> dictData = dictService.getDictData(dictType);
                String dictLabel = dictData.stream().filter(d -> value.equals(d.getDictValue())).findAny().map(SysDictData::getDictLabel).orElse(value);
                return new WriteCellData<>(dictLabel);
            }
        }
        return new WriteCellData<>(value);
    }
}
