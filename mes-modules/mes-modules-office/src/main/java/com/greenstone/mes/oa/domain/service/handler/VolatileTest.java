package com.greenstone.mes.oa.domain.service.handler;

/**
 * @author wushaoqi
 * @date 2024-04-08-11:29
 */
public class VolatileTest {
    volatile boolean flag = true;

    public void updateFlag() {
        this.flag = false;
        System.out.println("修改flag值为：" + false);
    }

    public static void main(String[] args) {
        VolatileTest test = new VolatileTest();
        new Thread(() -> {
            while (test.flag) {
            }
            System.out.println(Thread.currentThread().getName() + "结束");
        }, "Thread1").start();

        new Thread(() -> {
            try {
                Thread.sleep(2000);
                test.updateFlag();
            } catch (InterruptedException e) {
            }
        }, "Thread2").start();
    }
}

