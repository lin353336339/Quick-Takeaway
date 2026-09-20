package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.IndexConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.*;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Override
    public void saveWithDish(SetmealDTO setmealDTO) {
        //保存套餐数据到setmeal表
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //保存菜品数据到dish表
        setmealMapper.insert(setmeal);
        //获取套餐id
        Long setmealId = setmeal.getId();

        //批量增加菜品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && setmealDishes.size() > 0){
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDish.setSetmealId(setmealId);
            }
            log.info("setmealDishes：{}", setmealDishes);
            setmealDishMapper.insertBatch(setmealDishes);
        }


    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }
    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        setmealDishMapper.deleteBatchBySetmealId(ids);
        setmealMapper.deleteBatch(ids);
    }
    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Override
    public void update(SetmealDTO setmealDTO) {
        //获取套餐数据
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //修改套餐数据
        setmealMapper.update(setmeal);
        //覆盖口味数据
        Long setmealId = setmeal.getId();
        setmealDishMapper.deleteBySetmealId(setmealId);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && setmealDishes.size() > 0){
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDish.setSetmealId(setmealDTO.getId());
            }
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }
    /**
     * 根据id查询套餐
     * @param id
     * @return
     */

    @Override
    public SetmealDTO getById(Long id) {
        //查询套餐数据
        SetmealDTO setmealDTO = setmealMapper.getById(id);
        //查询套餐菜品数据
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
        setmealDTO.setSetmealDishes(setmealDishes);
        return setmealDTO;
    }

    /**
     * 套餐起售停售
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        //创建一个Setmeal对象
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {

        return setmealMapper.getDishItemBySetmealId(id);
    }

    /**
     * 获取套餐列表
     * @return
     */
    @Override
    public List<SetmealVO> listall() {
        List<Setmeal> list = setmealMapper.listall(StatusConstant.ENABLE);
        List<SetmealVO> listVO = new ArrayList<>();
        for (Setmeal setmeal : list){
            SetmealVO setmealVO = new SetmealVO();
            BeanUtils.copyProperties(setmeal,setmealVO);
            //获取起始日期
            LocalDate today = LocalDate.now();
            LocalDate DaysAgo = today.minusDays(IndexConstant.DaysAgo);
            LocalDateTime beginTime = LocalDateTime.of(DaysAgo, LocalTime.MIN);

            //获取本月订单
            List<Long> ids = orderMapper.getIdsByData(beginTime, Orders.COMPLETED);
            //计算月售
            Integer Sales = orderDetailMapper.getCountSetmeal(setmeal.getId(),ids);
            setmealVO.setSales(Sales);
            listVO.add(setmealVO);

            //获取套餐的菜品
            List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(setmeal.getId());
            setmealVO.setSetmealDishes(setmealDishes);
        }

        return listVO;
    }

    /**
     * 根据分类id查询套餐
     * @param categoryId
     * @return
     */
    @Override
    public List<SetmealVO> listByCategoryId(Long categoryId) {
        List<Setmeal> list = setmealMapper.listByCategoryId(StatusConstant.ENABLE,  categoryId);
        List<SetmealVO> listVO = new ArrayList<>();
        for (Setmeal setmeal : list){
            SetmealVO setmealVO = new SetmealVO();
            BeanUtils.copyProperties(setmeal,setmealVO);
            //获取起始日期
            LocalDate today = LocalDate.now();
            LocalDate DaysAgo = today.minusDays(IndexConstant.DaysAgo);
            LocalDateTime beginTime = LocalDateTime.of(DaysAgo, LocalTime.MIN);

            //获取本月订单
            List<Long> ids = orderMapper.getIdsByData(beginTime, Orders.COMPLETED);
            //计算月售
            Integer Sales = orderDetailMapper.getCountSetmeal(setmeal.getId(),ids);
            setmealVO.setSales(Sales);
            listVO.add(setmealVO);

            //获取套餐的菜品
            List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(setmeal.getId());
            setmealVO.setSetmealDishes(setmealDishes);
        }
        return listVO;
    }

}
