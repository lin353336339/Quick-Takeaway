package com.Quick.service.impl;

import com.Quick.context.BaseContext;
import com.Quick.dto.ShoppingCartDTO;
import com.Quick.entity.Dish;
import com.Quick.entity.Setmeal;
import com.Quick.entity.ShoppingCart;
import com.Quick.mapper.DishMapper;
import com.Quick.mapper.SetmealMapper;
import com.Quick.mapper.ShoppingCartMapper;
import com.Quick.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车功�?
     * @param shoppingCartDTO
     */
    @Override
    public void addshopping(ShoppingCartDTO shoppingCartDTO) {
        //判断购物车中商品是否存在
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        //如果存在，数量加一
        List<ShoppingCart> listshopping = shoppingCartMapper.listshopping(shoppingCart);
        if (listshopping != null && listshopping.size() > 0){
            ShoppingCart cart = listshopping.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.updateByUserId(cart);
        }
        //不存在，添加到购物车
        else if (shoppingCartDTO.getDishId() != null && shoppingCartDTO.getSetmealId() == null){
            Dish dish = dishMapper.getByid(shoppingCartDTO.getDishId());
            shoppingCart.setName(dish.getName());
            shoppingCart.setImage(dish.getImage());
            shoppingCart.setAmount(dish.getPrice());
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }
        else if (shoppingCartDTO.getSetmealId() != null && shoppingCartDTO.getDishId()  == null){
            Setmeal setmeal = setmealMapper.getByid(shoppingCartDTO.getSetmealId());
            shoppingCart.setName(setmeal.getName());
            shoppingCart.setImage(setmeal.getImage());
            shoppingCart.setAmount(setmeal.getPrice());
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    /**
     * 减少购物�?
     * @param shoppingCartDTO
     */
    @Override
    public void subshopping(ShoppingCartDTO shoppingCartDTO) {
        //判断购物车中商品是否存在
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        //如果存在，数量减一
        List<ShoppingCart> listshopping = shoppingCartMapper.listshopping(shoppingCart);
        if (listshopping != null && listshopping.size() > 0){
            ShoppingCart cart = listshopping.get(0);
            if (cart.getNumber() == 1){
                shoppingCartMapper.deleteByUserId(userId);
            }else if (cart.getNumber() > 1){
                cart.setNumber(cart.getNumber() - 1);
                shoppingCartMapper.updateByUserId(cart);
            }
        }
    }

    /**
     * 删除购物�?
     * @param shoppingCartDTO
     */
    @Override
    public void delete(ShoppingCartDTO shoppingCartDTO) {
        if (shoppingCartDTO.getSetmealId() == null && shoppingCartDTO.getDishId() != null){
            shoppingCartMapper.deleteByDishId(shoppingCartDTO.getDishId(), BaseContext.getCurrentId());
        }else if (shoppingCartDTO.getDishId() == null && shoppingCartDTO.getSetmealId() != null){
            shoppingCartMapper.deleteBySetmealId(shoppingCartDTO.getSetmealId(),BaseContext.getCurrentId());
        }
    }

    /**
     * 获取购物车列�?
     * @return
     */
    @Override
    public List<ShoppingCart> listShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> listShopping = shoppingCartMapper.listByUserId(userId);
        return listShopping;
    }

    /**
     * 清空购物�?
     */
    @Override
    public void clean() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);
    }




}
