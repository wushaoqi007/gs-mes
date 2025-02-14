package com.greenstone.mes.common.core.web.page;

import com.github.pagehelper.Page;

import java.io.Closeable;

/**
 * @author gu_renkai
 * @date 2023/2/14 8:59
 */
public record LocalPage<T>(Page<T> page) implements Closeable {

    public Page<T> getPage() {
        return page;
    }

    public static <T> LocalPage<T> of(Page<T> page) {
        return new LocalPage<>(page);
    }

    @Override
    public void close() {
        LocalPageHelper.clearLocalPage();
    }
}
