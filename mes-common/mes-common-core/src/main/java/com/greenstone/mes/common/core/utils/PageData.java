package com.greenstone.mes.common.core.utils;

import java.util.List;

public class PageData {

    private List<?> resultList;

    private List<?> pageList;

    public PageData() {
    }

    public PageData(List<?> resultList, List<?> pageList) {
        this.resultList = resultList;
        this.pageList = pageList;
    }

    public List<?> getResultList() {
        return resultList;
    }

    public void setResultList(List<?> resultList) {
        this.resultList = resultList;
    }

    public List<?> getPageList() {
        return pageList;
    }

    public void setPageList(List<?> pageList) {
        this.pageList = pageList;
    }
}
