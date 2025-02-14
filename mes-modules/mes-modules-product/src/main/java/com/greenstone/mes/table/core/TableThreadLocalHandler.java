package com.greenstone.mes.table.core;

import cn.hutool.core.util.NumberUtil;
import com.greenstone.mes.common.core.utils.SpringUtils;
import com.greenstone.mes.table.TableEntity;
import com.greenstone.mes.table.TablePo;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TableThreadLocalHandler<E extends TableEntity, P extends TablePo> implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getServletPath();
        String[] strings = path.split("/");
        for (int i = 0; i < strings.length; i++) {
            if (i <= 1) {
                if (strings[i].equals("tables")){
                    FunctionServiceHelper<E, P> functionServiceHelper = SpringUtils.getBean(FunctionServiceHelper.class);
                    if (NumberUtil.isNumber(strings[i + 1])){
                        FunctionModel<E, P> model = functionServiceHelper.getService(strings[i + 1]);
                        SpringUtils.getBean(TableThreadLocal.class).set(model);
                        break;
                    }
                }
            } else {
                break;
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        SpringUtils.getBean(TableThreadLocal.class).clear();
    }
}
