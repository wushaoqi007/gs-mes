package com.greenstone.mes.common.core.utils;

import com.github.pagehelper.PageHelper;
import com.greenstone.mes.common.core.constant.HttpStatus;
import com.greenstone.mes.common.core.utils.sql.SqlUtil;
import com.greenstone.mes.common.core.web.page.PageDomain;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.core.web.page.TableSupport;

import java.util.List;

/**
 * 分页工具类
 *
 * @author ruoyi
 */
public class PageUtils {
    /**
     * 设置请求分页数据
     */
    public static void startPage() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            Boolean reasonable = pageDomain.getReasonable();
            PageHelper.startPage(pageNum, pageSize, orderBy).setReasonable(reasonable);
        }
    }

    public static TableDataInfo buildPage(List<?> listData, long total) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setData(listData);
        rspData.setMsg("查询成功");
        rspData.setTotal(total);
        return rspData;
    }
}
