package com.Quick.mapper;

import com.github.pagehelper.Page;
import com.Quick.dto.OrdersPageQueryDTO;
import com.Quick.entity.OrderDetail;
import com.Quick.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 根据订单号查询订�?
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 订单分页查询
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> listOrder(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 根据状态统计订单数�?
     * @param toBeConfirmed
     * @return
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countByStatus(Integer toBeConfirmed);

    /**
     * 用户端分页查�?
     * @param status
     * @return
     */
    @Select("select * from orders where status = #{status}")
    Page<Orders> userlistOrder(Integer status);


    /**
     * 根据时间统计营业�?
     * @param map
     * @return
     */
    Double getTurnoverByDate(Map map);

    /**
     * 根据时间统计订单数量
     * @param map
     * @return
     */
    Integer countOrder(Map map);

    /**
     * 根据时间统计用户数量
     * @param map
     * @return
     */
    List<Long> listOrderBymap(Map map);

    /**
     * 根据条件统计订单数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);

    /**
     * 根据日期统计订单数量
     * @param begin
     * @return
     */
    Double getOrderCountByMap(LocalDate begin);

    /**
     * 查询所有订�?
     * @return
     */
    List<Long> listOrderall();

    /**
     * 获取当月订单id
     * @param beginTime
     * @return
     */
    List<Long> getIdsByData(LocalDateTime beginTime,  Integer status);
}
