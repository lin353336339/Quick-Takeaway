package com.Quick.service.impl;

import com.Quick.constant.IndexConstant;
import com.Quick.context.BaseContext;
import com.Quick.dto.GoodsSalesDTO;
import com.Quick.entity.Dish;
import com.Quick.entity.OrderDetail;
import com.Quick.entity.Orders;
import com.Quick.entity.Setmeal;
import com.Quick.mapper.DishMapper;
import com.Quick.mapper.OrderDetailMapper;
import com.Quick.mapper.OrderMapper;
import com.Quick.mapper.SetmealMapper;
import com.Quick.service.IndexService;
import com.Quick.vo.BusinessStoreVO;
import com.Quick.vo.CarouselVO;
import com.Quick.vo.GoodSalesUserVO;
import com.Quick.vo.SalesTop10ReportVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 营业数据统计
     * @return
     */
    @Override
    public BusinessStoreVO getBusinessStore() {
        //获取起始日期
        LocalDate today = LocalDate.now();
        LocalDate DaysAgo = today.minusDays(IndexConstant.DaysAgo);
        LocalDateTime beginTime = LocalDateTime.of(DaysAgo, LocalTime.MIN);

        Map map = new HashMap();
        map.put("begin",beginTime);

        //private Double orderCount;
        // 获取月订单数�?
        Integer OrderCount = orderMapper.countByMap(map);
        // 获取月有效订单数�?
        map.put("status",Orders.COMPLETED);
        Integer validOrderCount = orderMapper.countByMap(map);

        //private Double orderCompletionRate;
        //订单完成�?
        Double orderCompletionRate = validOrderCount.doubleValue()/OrderCount;

        //private Integer validOrderCount;
        //用户订单�?
        map.put("userId", BaseContext.getCurrentId());
        Integer userOrderCount = orderMapper.countByMap(map);

        //封装VO
        BusinessStoreVO businessStoreVO = BusinessStoreVO.builder()
                .orderCount(OrderCount)
                .orderCompletionRate(orderCompletionRate)
                .userOrderCount(userOrderCount)
                .build();
        return businessStoreVO;
    }

    /**
     * 获取轮播图数�?
     * @return
     */
    @Override
    public CarouselVO getCarousel() {
        //获取起始日期
        LocalDate today = LocalDate.now();
        LocalDate DaysAgo = today.minusDays(IndexConstant.DaysAgo);
        LocalDateTime beginTime = LocalDateTime.of(DaysAgo, LocalTime.MIN);

        Map map = new HashMap();
        map.put("begin",beginTime);
        map.put("status",Orders.COMPLETED);

//        private Dish Newdish;
        // 最新创建菜�?
        Dish Newdish = dishMapper.getNewDish();
//        private Dish mostSalesDish;//最多销量菜�?
        List<Long> ids = orderMapper.listOrderBymap(map);
        Dish mostSalesDish = dishMapper.getmostSalesDish(ids);
//        private Setmeal mostSalesSetmeal;//最多销量套�?
        Setmeal mostSalesSetmeal = orderDetailMapper.getMostSalesSetmeal(ids);


        CarouselVO carouselVO = CarouselVO.builder()
                .Newdish(Newdish)
                .mostSalesDish(mostSalesDish)
                .mostSalesSetmeal(mostSalesSetmeal)
                .build();
        return carouselVO;
    }

    /**
     * 销量排�?
     * @return
     */
    @Override
    public List<GoodSalesUserVO> gettop10() {
        //获取起始日期
        LocalDate today = LocalDate.now();
        LocalDate DaysAgo = today.minusDays(IndexConstant.DaysAgo);
        LocalDateTime beginTime = LocalDateTime.of(DaysAgo, LocalTime.MIN);

        Map map = new HashMap();
        map.put("begin",beginTime);
        map.put("status",Orders.COMPLETED);
        //查询指定订单id
        List<Long> ids = orderMapper.listOrderBymap(map);
        List<GoodsSalesDTO> goodsSalesDTO = orderDetailMapper.listByOrderIdsTop10(ids);
        List<GoodSalesUserVO> goodSalesUserVOS = new ArrayList<>();
        for (GoodsSalesDTO goodsSales : goodsSalesDTO){
            GoodSalesUserVO goodSalesUserVO = new GoodSalesUserVO();
            if (goodsSales.getDishId()!=null){
                Dish dish = dishMapper.getByid(goodsSales.getDishId());
                if (dish!=null){
                    goodSalesUserVO.setCategoryId(dish.getCategoryId());
                }else {
                    goodSalesUserVO.setCategoryId(0L);
                }
            }
            if (goodsSales.getSetmealId()!=null){
                Setmeal setmeal = setmealMapper.getByid(goodsSales.getSetmealId());
                if (setmeal!=null){
                    goodSalesUserVO.setCategoryId(setmeal.getCategoryId());
                }else {
                    goodSalesUserVO.setCategoryId(0L);
                }
            }
            BeanUtils.copyProperties(goodsSales,goodSalesUserVO);
            goodSalesUserVOS.add(goodSalesUserVO);
        }
        return goodSalesUserVOS;
    }
}
