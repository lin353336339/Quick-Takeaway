package com.Quick.mapper;

import com.Quick.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 动态条件查�?
     * @param shoppingCart
     */
    List<ShoppingCart> listshopping(ShoppingCart shoppingCart);

    /**
     * 根据用户id查询
     * @param userId
     */
    @Select("select * from shopping_cart where user_id = #{userId}")
    List<ShoppingCart> listByUserId(Long  userId);

    /**
     * 根据用户id修改商品信息
     * @param cart
     */
    void updateByUserId(ShoppingCart cart);

    /**
     * 插入购物车商品数�?
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time) "
            + "values (#{name}, #{image},  #{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{createTime})"
    )
    void insert(ShoppingCart shoppingCart);

    /**
     * 根据用户id清空购物车数�?
     * @param userId
     */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);

    /**
     * 批量插入购物车数�?
     * @param shoppingCartList
     */
    void insertBatch(List<ShoppingCart> shoppingCartList);

    /**
     * 根据菜品id删除购物车数�?
     * @param dishId
     */
    @Delete("delete from shopping_cart where dish_id = #{dishId} and user_id = #{userId}")
    void deleteByDishId(Long dishId, Long userId);

    /**
     * 根据套餐id删除购物车数�?
     * @param setmealId
     */
    @Delete("delete from shopping_cart where setmeal_id = #{setmealId} and user_id = #{userId}")
    void deleteBySetmealId(Long setmealId, Long userId);
}
