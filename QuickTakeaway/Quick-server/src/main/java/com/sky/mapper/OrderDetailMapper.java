package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Dish;
import com.sky.entity.OrderDetail;
import com.sky.entity.Setmeal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderDetailMapper {
    /**
     * 批量插入订单数据
     * @param orderDetailList
     */
    void insertBatch(List<OrderDetail> orderDetailList);

    /**
     * 根据订单id查询订单详情
     * @param id
     * @return
     */
    @Select("select * from order_detail where order_id = #{id}")
    List<OrderDetail> listByOrderId(Long id);

    /**
     * 根据订单id批量删除订单详情
     * @param ids
     */
    List<GoodsSalesDTO> listByOrderIdsTop10(List<Long> ids);

    /**
     * 获取最佳的销量菜品
     * @param ids
     */
    Dish getmostSalesDish(List<Long> ids);

    /**
     * 获取最佳的销量套餐
     * @param ids
     */
    Setmeal getMostSalesSetmeal(List<Long> ids);

    /**
     * 根据菜品id查询菜品的数量
     * @return
     */
    Integer getCountDish(Long dishId,List<Long> ids);

    /**
     * 根据套餐id查询套餐的数量
     * @return
     */
    Integer getCountSetmeal(Long id, List<Long> ids);
}
