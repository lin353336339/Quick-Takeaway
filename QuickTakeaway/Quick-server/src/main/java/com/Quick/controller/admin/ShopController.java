package com.Quick.controller.admin;

import com.Quick.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Api(tags = "店铺相关接口")
public class ShopController {

     @Autowired
     private RedisTemplate redisTemplate;

    public static final String Key = "SHOP_STATUS";


    /**
     * 设置店铺的营业状�?
     * @param status
     * @return
     */
     @PutMapping("/{status}")
     public Result setStatus(@PathVariable Integer status){
         log.info("设置店铺的营业状�?{}",status);
         redisTemplate.opsForValue().set("Key",status);
         return Result.success();
     }

    /**
     * 获取店铺的营业状�?
     * @return
     */
     @GetMapping("/status")
     @ApiOperation("获取店铺的营业状态")
     public Result<Integer> getStatus(){
         Integer status = (Integer) redisTemplate.opsForValue().get("Key");
         log.info("获取店铺的营业状�?{}",status);
         return Result.success(status);
     }
}
