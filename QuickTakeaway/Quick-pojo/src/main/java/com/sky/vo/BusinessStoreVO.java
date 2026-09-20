package com.sky.vo;

import io.swagger.models.auth.In;
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
public class BusinessStoreVO implements Serializable {

    private Integer orderCount;//月售

    private Double orderCompletionRate;//订单完成率

    private Integer userOrderCount;//用户订单数

}
