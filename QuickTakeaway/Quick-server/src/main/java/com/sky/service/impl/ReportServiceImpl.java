package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.entity.User;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.OrderService;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
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
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper  userMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //创建实体类对象
        TurnoverReportVO  turnoverReportVO = new TurnoverReportVO();
        //封装日期
        List<LocalDate> dateList = dateList(begin,end);
        String dateListStr = StringUtils.join(dateList,",");
        turnoverReportVO.setDateList(dateListStr);

        //封装营业额
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            //查询数据
            Map map = new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.getTurnoverByDate(map);
            turnover = turnover == null?0.0:turnover;
            turnoverList.add(turnover);
        }

        //封装营业额
        String  turnoverListStr = StringUtils.join(turnoverList,",");
        turnoverReportVO.setTurnoverList(turnoverListStr);

        return turnoverReportVO;
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //创建实体类对象
        UserReportVO  userReportVO = new UserReportVO();
        //封装日期
        List<LocalDate> dateList = dateList(begin,end);
        String dateListStr = StringUtils.join(dateList,",");
        userReportVO.setDateList(dateListStr);
        //封装新增用户数量
        List<Integer> newUserList = new ArrayList<>();
        for (LocalDate date : dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            //查询数据
            Map map = new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            Integer newUser = userMapper.countUser(map);
            newUser = newUser == null?0:newUser;
            newUserList.add(newUser);
        }
        //添加新增用户数量
        String  newUserListStr = StringUtils.join(newUserList,",");
        userReportVO.setNewUserList(newUserListStr);

        //封装总用户数量
        List<Integer> totalUserList = new ArrayList<>();
        for (LocalDate date : dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);

            Map map = new HashMap();
            map.put("end",end);
            totalUserList.add(userMapper.countUser(map));
            String totalUserListStr = StringUtils.join(totalUserList,",");
            userReportVO.setTotalUserList(totalUserListStr);
        }
        return userReportVO;
    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        //封装日期
        List<LocalDate> dateList = dateList(begin,end);
        String dateListStr = StringUtils.join(dateList,",");
        //封装订单数量
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();

        for (LocalDate date : dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            //查询数据
            Map map = new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);

            //查询订单总数
            Integer totalOrderCount = orderMapper.countOrder(map);
            totalOrderCount = totalOrderCount == null?0:totalOrderCount;
            orderCountList.add(totalOrderCount);

            //查询有效订单数量
            map.put("status", Orders.COMPLETED);
            Integer validOrderCount = orderMapper.countOrder(map);
            validOrderCount = validOrderCount == null?0:validOrderCount;
            validOrderCountList.add(validOrderCount);
        }

        //计算订单总数
        Integer ordersum = 0;
        for (Integer totalOrderCount : orderCountList){
            ordersum += totalOrderCount;
        }

        //计算有效订单数量
        Integer validOrderSum = 0;
        for (Integer validOrderCount : validOrderCountList){
            validOrderSum += validOrderCount;
        }

        Double orderCompletionRate = 0.0;
        if (ordersum != 0){
            orderCompletionRate = validOrderSum.doubleValue()/ordersum.doubleValue();
        }
        return OrderReportVO.builder()
                .dateList(dateListStr)
                .orderCountList(StringUtils.join(orderCountList,","))
                .validOrderCountList(StringUtils.join(validOrderCountList,","))
                .totalOrderCount(ordersum)
                .validOrderCount(validOrderSum)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 销量排名
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {
        //获取指定日期订单
        List<GoodsSalesDTO>  goodsSalesDTOS = new ArrayList<>();
        LocalDateTime  beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        Map map = new HashMap();
        map.put("begin",beginTime);
        map.put("end",endTime);
        map.put("status",Orders.COMPLETED);
        //查询指定订单id
        List<Long> ids = orderMapper.listOrderBymap(map);
        if (ids == null || ids.size() == 0){
            ids.add(-1L);
        }
        List<GoodsSalesDTO> goodsSalesDTO = orderDetailMapper.listByOrderIdsTop10(ids);
        List<String> names = goodsSalesDTO.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numbers = goodsSalesDTO.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());


        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(names,","))
                .numberList(StringUtils.join(numbers,","))
                .build();
    }

    /**
     * 封装日期集合
     * @param begin
     * @param end
     * @return
     */
    public List<LocalDate> dateList(LocalDate begin, LocalDate end){
        //创建两个list集合用于封装日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        return dateList;
    }
}
