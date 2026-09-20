package com.sky.service;


import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDTO
     */
    void saveWithDish(SetmealDTO setmealDTO);

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 删除套餐
     * @param ids
     */
    void deleteBatch(List<Long> ids);



    /**
     * 根据id修改套餐
     * @param
     * @return
     */
    void update(SetmealDTO setmealDTO);

    /**
     * 根据id查询套餐和套餐菜品关系
     * @param id
     * @return
     */
    SetmealDTO getById(Long id);

    /**
     * 起售停售套餐
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);

    /**
     * 查询所有套餐
     * @return
     */
    List<SetmealVO> listall();

    /**
     * 根据分类id查询套餐
     * @param categoryId
     * @return
     */
    List<SetmealVO> listByCategoryId(Long categoryId);
}
