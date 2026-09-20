package com.Quick.controller.user;

import com.Quick.entity.AddressBook;
import com.Quick.result.Result;
import com.Quick.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user/addressBook")
@Api(tags = "地址簿相关接口")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增地址�?
     * @param addressBook
     * @return
     */
    @ApiOperation("新增地址")
    @PostMapping
    public Result save(@RequestBody AddressBook addressBook) {
        log.info("新增地址簿：{}", addressBook);
        addressBookService.save(addressBook);
        return Result.success();
    }

    /**
     * 查询当前登录用户的地址
     * @return
     */
    @ApiOperation("查询当前登录用户的地址")
    @GetMapping("/list")
    public Result<List<AddressBook>> listResult(){
        log.info("查询地址");
        return Result.success(addressBookService.list());
    }

    /**
     * 查询当前登录用户默认的地址
     * @return
     */
    @ApiOperation("查询当前登录用户默认的地址")
    @GetMapping("/default")
    public Result<AddressBook> QueryDefaultAddressBook(){
        log.info("查询当前登录用户的地址");
        return Result.success(addressBookService.GetAddressBookByDefault());
    }

    /**
     * 根据ID查询地址
     * @return
     */
    @ApiOperation("根据ID查询地址")
    @GetMapping("/{id}")
    public Result<AddressBook> GetAddressBookById(@PathVariable Long id){
        log.info("根据ID查询地址");
        return Result.success(addressBookService.GetAddressBookById(id));
    }

    /**
     * 设置默认地址
     * @return
     */
    @ApiOperation("设置默认地址")
    @PutMapping("/default")
    public Result SetDefaultAddressBook(@RequestBody AddressBook addressBook){
        log.info("设置默认地址");
        Long id = addressBook.getId();
        addressBookService.SetDefaultAddressBook(id);
        return Result.success();
    }

    /**
     * 根据id修改地址
     * @param addressBook
     * @return
     */
    @PutMapping
    @ApiOperation("根据id修改地址")
    public Result update(@RequestBody AddressBook addressBook) {
        log.info("修改地址：{}", addressBook);
        addressBookService.update(addressBook);
        return Result.success();
    }

    /**
     * 删除地址
     * @return
     */
    @ApiOperation("删除地址")
    @DeleteMapping
    public Result delete(@RequestParam Long id){
        log.info("删除地址：{}", id);
        addressBookService.DelAddressBook(id);
        return Result.success();
    }
}
