package com.Quick.controller.user;

import com.Quick.constant.StatusConstant;
import com.Quick.entity.Dish;
import com.Quick.result.Result;
import com.Quick.service.DishService;
import com.Quick.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C“菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate  redisTemplate;

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    @Cacheable(cacheNames = "dishCache", key = "#categoryId")
    public Result<List<DishVO>> list(Long categoryId) {
        // 缓存优化
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品

        List<DishVO> list = dishService.listWithFlavor(dish);

        return Result.success(list);

    }

    /**
     * 查询所有菜
     * @return
     */
    @GetMapping("/all")
    @ApiOperation("查询所有菜品")
    public Result<List<DishVO>> all() {
        // 缓存优化
        String  key = "dish_all";
        List<DishVO> dishVOList = (List<DishVO>) redisTemplate.opsForValue().get("dish_all");
        if (dishVOList != null && dishVOList.size() > 0) {
            return Result.success(dishVOList);
        }

        List<DishVO> list = dishService.listallWithFlavor();

        redisTemplate.opsForValue().set(key, list);

        return Result.success(list);

    }

}
