package com.Quick.container;

import com.Quick.entity.DelayItem;
import com.Quick.entity.Orders;

import java.util.concurrent.DelayQueue;

public class CancelOrders implements Runnable{

    private DelayQueue<DelayItem<Orders>> delayQueue;

    private Orders orders;

    public CancelOrders(DelayQueue<DelayItem<Orders>> delayQueue, Orders orders) {
        this.delayQueue = delayQueue;
        this.orders = orders;
    }

    @Override
    public void run() {
        DelayItem<Orders> delayItem = new DelayItem<>(orders,20000L);
        delayQueue.add(delayItem);
    }
}
