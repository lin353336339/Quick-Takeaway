package com.sky.entity;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayItem<T> implements Delayed {
    //到期时间 毫秒
    private Long activeTime;
    //包装实际的实体类型
    private T item;

    public DelayItem(T item,Long activeTime){
        this.item = item;
        //获取到期时间到期时间
        this.activeTime = TimeUnit.NANOSECONDS.convert(activeTime,TimeUnit.MILLISECONDS)+System.nanoTime();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.activeTime-System.nanoTime(),TimeUnit.NANOSECONDS) ;
    }

    @Override
    public int compareTo(Delayed o) {
        Long time = getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
        return time == 0? 0:(time > 0?1:-1);
    }

    public T getItem() {
        return item;
    }
}
