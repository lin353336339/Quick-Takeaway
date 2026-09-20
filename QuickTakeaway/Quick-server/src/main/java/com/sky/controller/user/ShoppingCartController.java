package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Api(tags = "购物车接口")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService  shoppingCartService;

    /**
     * 添加商品卡片功能
     * @param shoppingCartDTO
     * @return
     */
    @ApiOperation("添加商品购物车")
    @PostMapping("/add")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加购物车:{}", shoppingCartDTO);
        shoppingCartService.addshopping(shoppingCartDTO);
        return Result.success();
    }
    /**
     * 查看购物车
     * @return
     */
    @ApiOperation("查看购物车")
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list() {
        log.info("查看购物车");
        List<ShoppingCart> list = shoppingCartService.listShoppingCart();
        return Result.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @ApiOperation("清空购物车")
    @DeleteMapping("/clean")
    public Result clean() {
        log.info("清空购物车");
        shoppingCartService.clean();
        return Result.success();
    }

    /**
     * 减少商品卡片
     * @param shoppingCartDTO
     * @return
     */
    @ApiOperation("减少商品购物车")
    @PostMapping("/sub")
    public Result sub(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("删除购物车:{}", shoppingCartDTO);
        shoppingCartService.subshopping(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 指定删除商品卡片
     * @param shoppingCartDTO
     * @return
     */
    @ApiOperation("指定删除商品购物车")
    @DeleteMapping("/delete")
    public Result delete(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("删除购物车:{}", shoppingCartDTO);
        shoppingCartService.delete(shoppingCartDTO);
        return Result.success();
    }

}
