package com.Quick.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoodsSalesDTO implements Serializable {
    //商品名称
    private String name;

    //销�?
    private Integer number;

    //菜品id
    private Long dishId;

    //套餐id
    private Long setmealId;

    //图片路径
    private String image;

    //单价
    private double amount;
}
