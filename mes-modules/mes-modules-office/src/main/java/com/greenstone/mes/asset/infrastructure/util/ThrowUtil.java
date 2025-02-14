package com.greenstone.mes.asset.infrastructure.util;

import com.greenstone.mes.common.core.enums.ServiceError;
import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * @author gu_renkai
 * @date 2023/2/3 16:38
 */
@Slf4j
public class ThrowUtil {

    public static <T> void nullThrow(Object o, ServiceError error) {
        if (o == null) {
            throw new ServiceException(error);
        }
    }

    public static <T> void presentThrow(Object o, ServiceError error) {
        if (o != null) {
            log.error("{},{}: {}", error.getCode(), error.getMsg(), o);
            throw new ServiceException(error);
        }
    }

    public static <T> void presentThrow(Supplier<T> supplier, ServiceError error) {
        T o = supplier.get();
        if (o != null) {
            throw new ServiceException(error);
        }
    }

    public static void trueThrow(boolean bool, ServiceError error) {
        if (bool) {
            throw new ServiceException(error);
        }
    }

}
