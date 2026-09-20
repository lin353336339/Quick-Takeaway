package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ShoppingCartService {

    /**
     * 添加购物车功能
     * @param shoppingCartDTO
     */
    void addshopping(ShoppingCartDTO shoppingCartDTO);

    /**
     * 获取购物车列表
     * @return
     */
    List<ShoppingCart> listShoppingCart();

    /**
     * 清空购物车
     */
    void clean();

    /**
     * 减少购物车
     * @param shoppingCartDTO
     */
    void subshopping(ShoppingCartDTO shoppingCartDTO);

    /**
     * 删除购物车
     * @param shoppingCartDTO
     */
    void delete(ShoppingCartDTO shoppingCartDTO);
}
