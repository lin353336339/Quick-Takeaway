package com.Quick.mapper;

import com.github.pagehelper.Page;
import com.Quick.annotation.AutoFill;
import com.Quick.dto.DishPageQueryDTO;
import com.Quick.entity.Dish;
import com.Quick.entity.SetmealDish;
import com.Quick.enumeration.OperationType;
import com.Quick.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品id查询对应的套餐id
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 插入套餐菜品关系数据
     * @param setmealDish
     * @return
     */
    void insert(SetmealDish setmealDish);

    /**
     * 批量插入套餐菜品数据
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id删除套餐菜品数据
     * @param id
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteBySetmealId(Long id);

    /**
     * 根据套餐id查询套餐菜品数据
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getBySetmealId(Long id);

    /**
     * 根据套餐id批量删除套餐菜品数据
     * @param ids
     */
    void deleteBatchBySetmealId(List<Long> ids);
}
