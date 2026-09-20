package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 数据概览
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodSalesUserVO implements Serializable {

    //商品名称
    private String name;

    //销量
    private Integer number;

    //菜品id
    private Long dishId;

    //套餐id
    private Long setmealId;

    //图片路径
    private String image;

    //单价
    private double amount;

    // 分类id
    private Long categoryId;

}
