package com.sky.vo;

import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
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
public class CarouselVO implements Serializable {

    private Dish Newdish;//最新创建菜品

    private Dish mostSalesDish;//最多销量菜品

    private Setmeal  mostSalesSetmeal;//最多销量套餐

}
