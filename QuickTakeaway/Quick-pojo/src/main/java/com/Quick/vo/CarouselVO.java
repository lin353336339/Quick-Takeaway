package com.Quick.vo;

import com.Quick.entity.Dish;
import com.Quick.entity.Setmeal;
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

    private Dish Newdish;//最新创建菜�?

    private Dish mostSalesDish;//最多销量菜�?

    private Setmeal  mostSalesSetmeal;//最多销量套�?

}
