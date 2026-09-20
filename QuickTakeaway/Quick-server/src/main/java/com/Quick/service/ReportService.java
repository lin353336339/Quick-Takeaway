package com.Quick.service;

import com.Quick.vo.OrderReportVO;
import com.Quick.vo.SalesTop10ReportVO;
import com.Quick.vo.TurnoverReportVO;
import com.Quick.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {

    /**
     * 营业额统�?
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);

    /**
     * 销量排�?
     * @param begin
     * @param end
     * @return
     */
    SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end);
}
