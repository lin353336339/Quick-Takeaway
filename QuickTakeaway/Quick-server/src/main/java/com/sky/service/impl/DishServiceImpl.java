package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.IndexConstant;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Orders;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    /**
     * 新增菜品
     * @param dishDTO
     */
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        //保存菜品数据到dish表
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //保存菜品数据到dish_flavor表
        dishMapper.insert(dish);
        //获取生成的主键值
        Long dishId = dish.getId();
        //保存菜品口味数据到dish_flavor表
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0){
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishId);
            }
            dishFlavorMapper.insertBatch(flavors);
        }

    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //开始分页查询
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        //判断菜品是否起售，起售不可删除
        for (Long id : ids){
            Dish dish = dishMapper.getByid(id);
            if (dish.getStatus() == StatusConstant.ENABLE){//启用状态 :1 启用
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //判断菜品是否关联套餐，关联套餐不可删除
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

//        for (Long id : ids){
//            dishMapper.deleteById(id);
//            dishFlavorMapper.deleteByDishId(id);
//        }
        //批量删除菜品数据
        dishMapper.deleteByIds(ids);
        dishFlavorMapper.deleteByDishIds(ids);
    }

    /**
     * 根据id查询菜品和对应的口味数据
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        //根据id查询菜品数据
        Dish dish = dishMapper.getByid(id);
        List<DishFlavor> dishFlavorList = dishFlavorMapper.getByDishId(id);

        //根据id查询口味数据
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavorList);


        return dishVO;
    }

    /**
     * 修改菜品
     * @param dishDTO
     */
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        //获取菜品数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        //修改菜品数据
        dishMapper.update(dish);
        //覆盖口味数据
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0){
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishDTO.getId());
            }
            dishFlavorMapper.insertBatch(flavors);
        }

    }

    /**
     * 启用、禁用菜品
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();

        dishMapper.update(dish);
    }

    /**
     * 根据分类id查询菜品
     * @param dish
     * @return
     */
    @Override
    public List<Dish> list(Dish dish) {
        return dishMapper.list(dish);
    }

    /**
     * 查询所有菜品和口味
     * @return
     */
    @Override
    public List<DishVO> listallWithFlavor() {
        List<Dish> dishList = dishMapper.listallByStatus(StatusConstant.ENABLE);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);


            //获取起始日期
            LocalDate today = LocalDate.now();
            LocalDate DaysAgo = today.minusDays(IndexConstant.DaysAgo);
            LocalDateTime beginTime = LocalDateTime.of(DaysAgo, LocalTime.MIN);

            //获取本月订单
            List<Long> ids = orderMapper.getIdsByData(beginTime, Orders.COMPLETED);
            //计算月售
            Integer Sales = orderDetailMapper.getCountDish(d.getId(),ids);
            dishVO.setSales(Sales);
            dishVOList.add(dishVO);
        }
        return dishVOList;
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);

            //获取起始日期
            LocalDate today = LocalDate.now();
            LocalDate DaysAgo = today.minusDays(IndexConstant.DaysAgo);
            LocalDateTime beginTime = LocalDateTime.of(DaysAgo, LocalTime.MIN);

            //获取本月订单
            List<Long> ids = orderMapper.getIdsByData(beginTime, Orders.COMPLETED);
            //计算月售
            Integer Sales = 0;
            try {
                Sales = orderDetailMapper.getCountDish(d.getId(),ids);
            } catch (Exception e) {
                log.error("查询菜品销量失败，菜品ID: {}", d.getId(), e);
                Sales = 0; // 设置默认值
            }

            dishVO.setSales(Sales);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

}
