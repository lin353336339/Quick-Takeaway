package com.Quick.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.Quick.constant.MessageConstant;
import com.Quick.constant.DelayConstant;
import com.Quick.constant.WebSocketConstant;
import com.Quick.container.CancelOrders;
import com.Quick.container.OrderDelayQueue;
import com.Quick.context.BaseContext;
import com.Quick.dto.*;
import com.Quick.entity.*;
import com.Quick.exception.AddressBookBusinessException;
import com.Quick.exception.OrderBusinessException;
import com.Quick.mapper.*;
import com.Quick.result.PageResult;
import com.Quick.service.OrderService;
import com.Quick.vo.OrderPaymentVO;
import com.Quick.vo.OrderStatisticsVO;
import com.Quick.vo.OrderSubmitVO;
import com.Quick.vo.OrderVO;
import com.Quick.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

/**
 * 订单
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private RedisTemplate  redisTemplate;
    @Autowired
    private OrderDelayQueue orderDelayQueue;
    @Autowired
    private WebSocketServer  webSocketServer;


    /**
     * 用户下单
     *
     * @param ordersSubmitDTO
     * @return
     */
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        //处理地址异常情况
        AddressBook  addressBook = addressBookMapper.GetAddressBookById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //处理购物车数据异常情�?
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.listByUserId(BaseContext.getCurrentId());
        if (shoppingCartList == null || shoppingCartList.size() == 0){
            throw new AddressBookBusinessException(MessageConstant.SHOPPING_CART_IS_EMPTY);
        }

        //构造订单数据并插入到数据库�?
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        orders.setUserId(BaseContext.getCurrentId());
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress(addressBook.getDetail());

        orderMapper.insert(orders);

        //构造订单明细数据并插入到数据库�?
        List<OrderDetail> orderDetailList = new ArrayList<>();

        for (ShoppingCart cart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart,orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);

        //清空购物�?
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());

        //封装vo对象并返回数�?
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();

        //添加延迟队列
        orderDelayQueue.addOrder(orders, DelayConstant.ORDER_PAYMENT_TIMEOUT);
        return orderSubmitVO;
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO){
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易�?
        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject = weChatPayUtil.pay(
//                    ordersPaymentDTO.getOrderNumber(), //商户订单�?
//                    new BigDecimal(0.01), //支付金额，单�?�?
//                    "苍穹外卖订单", //商品描述
//                    user.getOpenid() //微信用户的openid
//            );
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        paySuccess(ordersPaymentDTO.getOrderNumber());

        return vo;
    }

    /**
     * 支付成功，修改订单状�?
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订�?
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时�?
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);

        Map map = new HashMap();
        map.put("type", WebSocketConstant.WEBSOCKET_ORDER_COMMING);
        map.put("orderId", ordersDB.getId());
        map.put("content", "订单�?"+outTradeNo);

        String json = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);
    }

    // TODO 分页查询订单 可以降重
    /**
     * 订单分页查询
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult PageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        Page<Orders> page = orderMapper.listOrder(ordersPageQueryDTO);
        List<OrderVO> orderVOList = getOrderVOList(page);
        return new PageResult(page.getTotal(), orderVOList);
    }

    /**
     * 订单取消
     * @param ordersCancelDTO
     */
    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        // 获取订单状�?
        Orders orders = orderMapper.getById(ordersCancelDTO.getId());
        // 判断订单状�?如果为已经PAID(已经支付) 则退�?
        //ToDo 当前未实现支付功能，退款功能暂时取�?
        if (orders.getPayStatus() ==  Orders.PAID) {
            orders.setPayStatus(Orders.REFUND);
            log.info("退款：{}", orders.getPayStatus());
        }
        //  取消订单
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 订单详情查询
     * @param id
     * @return
     */
    @Override
    public OrderVO details(Long id) {
        //Order表查询订单数�?
        Orders orders = orderMapper.getById(id);
        //OrderDetail表查询订单菜品数�?
        List<OrderDetail> orderDetailList = orderDetailMapper.listByOrderId(orders.getId());
        //封装OrderVO
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    /**
     * 各个状态的订单数量统计
     * @return
     */
    @Override
    public OrderStatisticsVO statistics() {
        //查询待接单数�?TO_BE_CONFIRMED
        Integer toBeConfirmed = orderMapper.countByStatus(Orders.TO_BE_CONFIRMED);
        //查询待派�?已接单数�?
        Integer confirmed = orderMapper.countByStatus(Orders.CONFIRMED);
        //查询派送中数量 DELIVERY_IN_PROGRESS
        Integer deliveryInProgress = orderMapper.countByStatus(Orders.DELIVERY_IN_PROGRESS);
        //封装OrderStatisticsVO
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        return orderStatisticsVO;
    }

    /**
     * 接单
     * @param id
     */
    @Override
    public void confirm(Long id) {
        //查询当前订单
        Orders orders = orderMapper.getById(id);
        //修改订单状�?
        orders.setStatus(Orders.CONFIRMED);
        orderMapper.update(orders);
    }

    /**
     * 退�?
     * @param ordersRejectionDTO
     */
    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        // 获取订单状�?
        Orders orders = orderMapper.getById(ordersRejectionDTO.getId());
        // 判断订单状�?如果为已经PAID(已经支付) 则退�?
        //ToDo 当前未实现支付功能，退款功能暂时取�?
        if (orders.getPayStatus() ==  Orders.PAID) {
            orders.setPayStatus(Orders.REFUND);
            log.info("退款：{}", orders.getPayStatus());
        }
        //  取消订单
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 订单完成
     * @param id
     */
    @Override
    public void complete(Long id) {
        //查询订单
        Orders orders = orderMapper.getById(id);
        //修改订单状态：已完�?
        orders.setStatus(Orders.COMPLETED);
        //修改订单完成时间
        orders.setCheckoutTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 订单配�?
     * @param id
     */
    @Override
    public void delivery(Long id) {
        //查询订单
        Orders orders = orderMapper.getById(id);
        //修改订单状态：派送中
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);

        //添加订单到延迟队�?
        orderDelayQueue.addDeliveryOrder(orders, DelayConstant.ORDER_COMPLETETIME_TIMEOUT);

        orderMapper.update(orders);
    }

    /**
     * 用户端订单分页查�?
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public PageResult UserPageQuery(int page, int pageSize, Integer status) {
        // 封装查询条件
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);

        //分页查询
        PageHelper.startPage(page, pageSize);
        Page<Orders> pageUserOrder = orderMapper.listOrder(ordersPageQueryDTO);

        List<OrderVO> list = new ArrayList<>();
        // 查询出订单明细，并封装入OrderVO进行响应
        if (pageUserOrder != null && pageUserOrder.getTotal() > 0) {
            for (Orders orders : pageUserOrder) {
                Long orderId = orders.getId();// 订单id

                // 查询订单明细
                List<OrderDetail> orderDetails = orderDetailMapper.listByOrderId(orderId);

                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetails);

                list.add(orderVO);
            }
        }
        return new PageResult(pageUserOrder.getTotal(), list);
    }

    /**
     * 用户查询订单详情
     * @param id
     * @return
     */
    @Override
    public OrderVO Userdetails(Long id) {
        //Order表查询订单数�?
        Orders orders = orderMapper.getById(id);
        //OrderDetail表查询订单菜品数�?
        List<OrderDetail> orderDetailList = orderDetailMapper.listByOrderId(orders.getId());
        //封装OrderVO
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    /**
     * 用户取消订单
     * @param id
     */
    @Override
    public void Usercancel(Long id) {
        //查询当前订单
        Orders orders = orderMapper.getById(id);
        // 判断订单状�?
        // 校验订单是否存在
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //订单状�?1待付�?2待接�?3已接�?4派送中 5已完�?6已取�?
        if (orders.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        // 判断支付状�?
        if (orders.getPayStatus() == Orders.PAID) {
            orders.setPayStatus(Orders.REFUND);
        }
        //  取消订单
        // 更新订单状态、取消原因、取消时�?
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(MessageConstant.USER_CANCEL_ORDER);
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 再来一�?
     * @param id
     */
    @Override
    public void repetition(Long id) {
        Long Userid  = BaseContext.getCurrentId();
        // 根据id获取订单详情
        List<OrderDetail> orderDetailList = orderDetailMapper.listByOrderId(id);
        // 添加至购物车
        // 将订单详情对象转换为购物车对�?
        List<ShoppingCart> shoppingCartList = new ArrayList<>();

        for (OrderDetail orderDetail : orderDetailList){
            ShoppingCart shoppingCart = new ShoppingCart();

            // 将原订单详情里面的菜品信息重新复制到购物车对象中
            BeanUtils.copyProperties(orderDetail, shoppingCart, "id");
            shoppingCart.setUserId(Userid);
            shoppingCart.setCreateTime(LocalDateTime.now());

            shoppingCartList.add(shoppingCart);
        }
        shoppingCartMapper.insertBatch(shoppingCartList);
    }

    /**
     * 催单
     * @param id
     */
    @Override
    public void reminder(Long id) {
        Orders orders = orderMapper.getById(id);
        //  判断订单是否存在
        if (orders == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //  判断订单状�?
        Map map = new HashMap();
        map.put("type", WebSocketConstant.WEBSOCKET_ORDER_REMINDER);
        map.put("orderId", id);
        map.put("content", "订单�?" + orders.getNumber());
        //  发送WebSocket消息
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }
    /**
     * 封装订单菜品信息
     * @return
     */
    private List<OrderVO> getOrderVOList(Page<Orders> page) {
        // 需要返回订单菜品信息，自定义OrderVO响应结果
        List<OrderVO> orderVOList = new ArrayList<>();

        List<Orders> ordersList = page.getResult();
        if (ordersList != null && !ordersList.isEmpty()) {
            for (Orders orders : ordersList) {
                /// 获取订单菜品信息
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                String orderDishes = getOrderDishesStr(orders);

                /// 获取菜品信息，将菜品信息放入OrderVO
                orderVO.setOrderDishes(orderDishes);
                orderVOList.add(orderVO);
            }
        }
        return orderVOList;
    }

    private String getOrderDishesStr(Orders orders) {
        /// 查询订单菜品信息
        List<OrderDetail> orderDetailList = orderDetailMapper.listByOrderId(orders.getId());
        String orderDishes = new String();
        if (orderDetailList != null && orderDetailList.size() > 0) {
            for (OrderDetail orderDetail : orderDetailList){
                orderDishes = orderDishes + orderDetail.getName() + "*" + orderDetail.getNumber()+ ";";
            }
        }
        return orderDishes;
    }



}
