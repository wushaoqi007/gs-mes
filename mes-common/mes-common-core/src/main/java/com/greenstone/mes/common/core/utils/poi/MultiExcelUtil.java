package com.greenstone.mes.common.core.utils.poi;

import com.greenstone.mes.common.core.annotation.Excel;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.util.List;

public class MultiExcelUtil<T> extends ExcelUtil<T> {

    public MultiExcelUtil(Class<T> clazz) {
        super(clazz);
    }

    public void exportInit(){
        this.wb = new SXSSFWorkbook(500);
        this.styles = createStyles(wb);
        this.type = Excel.Type.EXPORT;
        super.createExcelField();
    }

    public void addSheet(List<T> list, String sheetName) {
        this.list = list;
        if (list.size() > sheetSize) {
            throw new RuntimeException("超出单页最大数据条数，最多65536条");
        }
        Sheet sheet = wb.createSheet(sheetName);
        this.sheet = sheet;

        // 产生一行
        Row row = sheet.createRow(rownum);
        int column = 0;
        // 写入各个字段的列头名称
        for (Object[] os : fields) {
            Excel excel = (Excel) os[1];
            this.createCell(excel, row, column++);
        }
        if (Excel.Type.EXPORT.equals(type)) {
            fillExcelData(0, row);
            addStatisticsRow();
        }
    }

}
