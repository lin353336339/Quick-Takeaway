package com.Quick.service;

import com.Quick.dto.ShoppingCartDTO;
import com.Quick.entity.ShoppingCart;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ShoppingCartService {

    /**
     * 添加购物车功�?
     * @param shoppingCartDTO
     */
    void addshopping(ShoppingCartDTO shoppingCartDTO);

    /**
     * 获取购物车列�?
     * @return
     */
    List<ShoppingCart> listShoppingCart();

    /**
     * 清空购物�?
     */
    void clean();

    /**
     * 减少购物�?
     * @param shoppingCartDTO
     */
    void subshopping(ShoppingCartDTO shoppingCartDTO);

    /**
     * 删除购物�?
     * @param shoppingCartDTO
     */
    void delete(ShoppingCartDTO shoppingCartDTO);
}
