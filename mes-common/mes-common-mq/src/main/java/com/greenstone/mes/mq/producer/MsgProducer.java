package com.greenstone.mes.mq.producer;

import java.util.concurrent.ExecutionException;

public interface MsgProducer<V> {

    void send(String topic, V object) throws ExecutionException, InterruptedException;

    void send(String topic, String tag, V object) throws ExecutionException, InterruptedException;

}
