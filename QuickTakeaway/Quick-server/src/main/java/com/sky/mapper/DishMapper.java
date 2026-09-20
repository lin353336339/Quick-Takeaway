package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 新增菜品数据
     * @param dishDTO
     */
    @AutoFill(OperationType.INSERT)
    void insert(Dish dishDTO);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id}")
    Dish getByid(Long id);

    /**
     * 根据id删除菜品数据
     * @param id
     */
    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据id批量删除菜品数据
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 根据id修改菜品数据
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 动态条件查询菜品
     * @param dish
     * @return
     */
    List<Dish> list(Dish dish);

    /**
     * 根据类别ID查询菜品
     * @param categoryId
     * @return
     */
    @Select("select * from dish where category_id = #{categoryId} and status = 1")
    List<Dish> listByCategoryId(Long categoryId);

    /**
     * 根据条件统计菜品数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);

    /**
     * 获取最新菜品
     * @return
     */
    @Select("SELECT * FROM dish ORDER BY create_time DESC LIMIT 1")
    Dish getNewDish();

    /**
     * 获取销量最高的菜品
     * @return
     */
    Dish getmostSalesDish(List<Long> ids);

    /**
     * 获取所有菜品
     * @return
     */
    @Select("select * from dish")
    List<Dish> listall();

    /**
     * 根据状态查询所有菜品
     * @param status
     * @return
     */
    @Select("select * from dish where status = #{status}")
    List<Dish> listallByStatus(Integer status);
}
