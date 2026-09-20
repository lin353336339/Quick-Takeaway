package com.Quick.controller.user;

import com.Quick.dto.GoodsSalesDTO;
import com.Quick.result.Result;
import com.Quick.service.IndexService;
import com.Quick.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/user/index")
@Api(tags = "C端用户首页接口")
@Slf4j
public class IndexController {

    @Autowired
    private IndexService indexService;

    /**
     * 统计营业数据
     * @return
     */
    @GetMapping("/businessStore")
    @ApiOperation("用户查询店铺相关相信")
    public Result<BusinessStoreVO> businessUser() {
        BusinessStoreVO businessStoreVO = indexService.getBusinessStore();
        return Result.success(businessStoreVO);
    }

    /**
     * 获取轮播图数�?
     * @return
     */
    @GetMapping("/Carousel")
    @ApiOperation("用户查询轮播图信息")
    public Result<CarouselVO> NavigationBar() {
        CarouselVO carouselVO = indexService.getCarousel();
        return Result.success(carouselVO);
    }

    /**
     * 店铺热门推荐
     * @return
     */
    @GetMapping("/top10")
    @ApiOperation("销量排名")
    public Result<List<GoodSalesUserVO>> top10(){
        log.info("销量排名：{}到{}");
        List<GoodSalesUserVO> goodsSalesDTOList = indexService.gettop10();
        return Result.success(goodsSalesDTOList);
    }


}
