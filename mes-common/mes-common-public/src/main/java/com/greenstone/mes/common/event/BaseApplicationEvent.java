package com.greenstone.mes.common.event;

import org.springframework.context.ApplicationEvent;

public class BaseApplicationEvent<E> extends ApplicationEvent {

    public BaseApplicationEvent(E source) {
        super(source);
    }

    @SuppressWarnings("unchecked")
    @Override
    public E getSource() {
        return (E) super.getSource();
    }

}
