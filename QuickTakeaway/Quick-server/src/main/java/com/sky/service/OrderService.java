package com.sky.service;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface OrderService {
    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 用户支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO);

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 订单分页查询
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult PageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 用户取消订单
     * @param ordersCancelDTO
     */
    void cancel(OrdersCancelDTO ordersCancelDTO);

    /**
     * 订单详情
     * @param id
     * @return
     */
    OrderVO details(Long id);

    /**
     * 各个状态的订单数量统计
     * @return
     */
    OrderStatisticsVO statistics();

    /**
     * 接单
     * @param id
     */
    void confirm(Long id);

    /**
     * 拒单
     * @param ordersCancelDTO
     */
    void rejection(OrdersRejectionDTO ordersCancelDTO);

    /**
     * 完成订单
     * @param id
     */
    void complete(Long id);

    /**
     * 派送订单
     * @param id
     */
    void delivery(Long id);

    /**
     * 用户端订单分页查询
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult UserPageQuery(int page, int pageSize, Integer status);

    /**
     * 用户端订单详情查询
     * @param id
     * @return
     */
    OrderVO Userdetails(Long id);

    /**
     * 用户端取消订单
     * @param id
     */
    void Usercancel(Long id);

    /**
     * 用户端重复下单
     * @param id
     */
    void repetition(Long id);


    /**
     * 订单催单
     * @param id
     */
    void reminder(Long id);
}
