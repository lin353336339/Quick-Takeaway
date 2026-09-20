package com.Quick.container;

import com.Quick.entity.DelayItem;
import com.Quick.entity.Orders;
import com.Quick.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 订单延时队列管理�?
 */
@Component
@Slf4j
public class OrderDelayQueue {

    // 延时队列
    private final DelayQueue<DelayItem<Orders>> delayQueue = new DelayQueue<>();

    // 线程�?- 消费者线�?
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 添加订单到延时队�?
     * @param orders 订单信息
     * @param delayTime 延时时间（毫秒）
     */
    public void addOrder(Orders orders, long delayTime) {
        DelayItem<Orders> delayItem = new DelayItem<>(orders, delayTime);
        delayQueue.offer(delayItem);
        log.info("订单{}已加入延时队列，将在{}毫秒后自动取消", orders.getId(), delayTime);
    }

    /**
     * 添加派送中订单到延时队�?
     * @param orders 订单信息
     * @param delayTime 延时时间（毫秒）
     */
    public void addDeliveryOrder(Orders orders, long delayTime) {
        DelayItem<Orders> delayItem = new DelayItem<>(orders, delayTime);
        // 设置派送订单标�?
        orders.setRemark("DELIVERY_TIMEOUT");
        delayQueue.offer(delayItem);
        log.info("派送订单{}已加入延时队列，将在{}毫秒后自动完成", orders.getId(), delayTime);
    }

    /**
     * 初始化消费者线�?
     */
    @PostConstruct
    public void init() {
        // 启动消费者线�?
        executorService.execute(() -> {
            log.info("订单延时队列处理线程已启");
            while (true) {
                try {
                    // 从延时队列中获取已到期的订单
                    DelayItem<Orders> delayItem = delayQueue.take();
                    Orders orders = delayItem.getItem();

                    // 根据订单备注判断是派送超时还是支付超�?
                    if ("DELIVERY_TIMEOUT".equals(orders.getRemark())) {
                        // 处理派送超时订�?
                        handleDeliveryTimeout(orders);
                    } else {
                        // 处理支付超时订单
                        handlePaymentTimeout(orders);
                    }
                } catch (InterruptedException e) {
                    log.error("订单延时队列处理线程被中断");
                    break;
                } catch (Exception e) {
                    log.error("处理超时订单异常", e);
                }
            }
        });
    }

    /**
     * 处理支付超时订单
     */
    private void handlePaymentTimeout(Orders orders) {
        log.info("订单{}支付超时，准备执行自动取消", orders.getId());

        // 查询订单当前状�?
        Orders currentOrder = orderMapper.getById(orders.getId());

        // 只有未支付的订单才自动取�?
        if (currentOrder != null && currentOrder.getStatus() == Orders.PENDING_PAYMENT) {
            // 更新订单状态为已取�?
            Orders updateOrder = Orders.builder()
                    .id(orders.getId())
                    .status(Orders.CANCELLED)
                    .cancelReason("订单超时未支付，系统自动取消")
                    .cancelTime(LocalDateTime.now())
                    .build();
            orderMapper.update(updateOrder);
            log.info("订单{}超时未支付，已自动取消", orders.getId());
        }
    }

    /**
     * 处理派送超时订�?
     */
    private void handleDeliveryTimeout(Orders orders) {
        log.info("订单{}派送超时，准备执行自动完成", orders.getId());

        // 查询订单当前状�?
        Orders currentOrder = orderMapper.getById(orders.getId());

        // 只有派送中的订单才自动完成
        if (currentOrder != null && currentOrder.getStatus() == Orders.DELIVERY_IN_PROGRESS) {
            // 更新订单状态为已完�?
            Orders updateOrder = Orders.builder()
                    .id(orders.getId())
                    .status(Orders.COMPLETED)
                    .build();
            orderMapper.update(updateOrder);
            log.info("订单{}派送超时，已自动完成", orders.getId());
        }
    }

    /**
     * 应用关闭时清理资�?
     */
    @PreDestroy
    public void destroy() {
        executorService.shutdown();
        log.info("订单延时队列处理线程已关闭");
    }
}