package com.Quick.service;

import com.Quick.dto.GoodsSalesDTO;
import com.Quick.vo.*;

import java.time.LocalDate;
import java.util.List;

public interface IndexService {

    /**
     * 统计营业数据
     * @return
     */
    BusinessStoreVO getBusinessStore();

    /**
     * 获取轮播图数�?
     * @return
     */
    CarouselVO getCarousel();

    /**
     * 销量排�?
     * @return
     */
    List<GoodSalesUserVO> gettop10();
}
