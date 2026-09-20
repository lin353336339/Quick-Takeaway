package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {

    /**
     * 新增地址簿
     * @param addressBook
     * @return
     */
    void save(AddressBook addressBook);

    /**
     * 查询所有地址簿
     * @return
     */
    List<AddressBook> list();

    /**
     * 查询默认地址
     * @return
     */
    AddressBook GetAddressBookByDefault();

    /**
     * 根据id查询地址簿
     * @param id
     * @return
     */
    AddressBook GetAddressBookById(Long id);

    /**
     * 设置默认地址
     * @param id
     */
    void SetDefaultAddressBook(Long id);

    /**
     * 修改地址簿
     * @param addressBook
     */
    void update(AddressBook addressBook);

    /**
     * 删除地址簿
     * @param id
     */
    void DelAddressBook(Long id);
}
