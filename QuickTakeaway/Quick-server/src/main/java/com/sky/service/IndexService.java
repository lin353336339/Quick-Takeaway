package com.sky.service;

import com.sky.dto.GoodsSalesDTO;
import com.sky.vo.*;

import java.time.LocalDate;
import java.util.List;

public interface IndexService {

    /**
     * 统计营业数据
     * @return
     */
    BusinessStoreVO getBusinessStore();

    /**
     * 获取轮播图数据
     * @return
     */
    CarouselVO getCarousel();

    /**
     * 销量排名
     * @return
     */
    List<GoodSalesUserVO> gettop10();
}
