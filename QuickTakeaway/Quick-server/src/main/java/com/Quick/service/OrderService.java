package com.Quick.service;

import com.Quick.constant.MessageConstant;
import com.Quick.context.BaseContext;
import com.Quick.dto.*;
import com.Quick.entity.AddressBook;
import com.Quick.entity.OrderDetail;
import com.Quick.entity.Orders;
import com.Quick.entity.ShoppingCart;
import com.Quick.result.PageResult;
import com.Quick.vo.OrderPaymentVO;
import com.Quick.vo.OrderStatisticsVO;
import com.Quick.vo.OrderSubmitVO;
import com.Quick.vo.OrderVO;

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
     * 支付成功，修改订单状�?
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
     * 派送订�?
     * @param id
     */
    void delivery(Long id);

    /**
     * 用户端订单分页查�?
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult UserPageQuery(int page, int pageSize, Integer status);

    /**
     * 用户端订单详情查�?
     * @param id
     * @return
     */
    OrderVO Userdetails(Long id);

    /**
     * 用户端取消订�?
     * @param id
     */
    void Usercancel(Long id);

    /**
     * 用户端重复下�?
     * @param id
     */
    void repetition(Long id);


    /**
     * 订单催单
     * @param id
     */
    void reminder(Long id);
}
