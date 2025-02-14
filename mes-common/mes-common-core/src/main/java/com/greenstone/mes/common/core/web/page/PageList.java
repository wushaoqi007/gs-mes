package com.greenstone.mes.common.core.web.page;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/12/21 16:52
 */

public class PageList<T> {

    private List<?> pageList;

    private List<T> resultList;

    public PageList(List<?> pageList, List<T> resultList) {
        this.pageList = pageList;
        this.resultList = resultList;
    }

    public static <T> PageList<T> of(List<?> pageList, List<T> rows) {
        return new PageList<>(pageList, rows);
    }

    public static <T> PageList<T> of(PageList<?> pageList, List<T> rows) {
        return new PageList<>(pageList.getPageList(), rows);
    }

    public List<?> getPageList() {
        return pageList;
    }

    public List<T> getResultList() {
        return resultList;
    }

}
