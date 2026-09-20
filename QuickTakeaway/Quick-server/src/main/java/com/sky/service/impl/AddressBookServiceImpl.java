package com.sky.service.impl;

import com.sky.constant.AddressBookConstant;
import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AddressBookServiceImpl implements com.sky.service.AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     * 新增地址簿
     * @param addressBook
     * @return
     */
    @Override
    public void save(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(AddressBookConstant.ISDEFAULT);
        addressBookMapper.insert(addressBook);
    }

    /**
     * 查询所有地址簿
     * @return
     */
    @Override
    public List<AddressBook> list() {
        List<AddressBook> list = addressBookMapper.list(BaseContext.getCurrentId());
        return list;
    }

    /**
     * 查询默认地
     * @return
     */
    @Override
    public AddressBook GetAddressBookByDefault() {
        AddressBook addressBook = addressBookMapper.GetAddressBookByDefault(BaseContext.getCurrentId(), AddressBookConstant.DEFAULT);
        return addressBook;
    }

    /**
     * 根据id查询地址簿
     * @param id
     * @return
     */
    @Override
    public AddressBook GetAddressBookById(Long id) {
        AddressBook addressBook = addressBookMapper.GetAddressBookById(id);
        return addressBook;
    }

    /**
     * 设置默认地址
     * @param id
     * @return
     */
    @Override
    public void SetDefaultAddressBook(Long id) {
        addressBookMapper.SetIsDefaultAddressBook(AddressBookConstant.ISDEFAULT);
        addressBookMapper.SetDefaultAddressBook(id, AddressBookConstant.DEFAULT);
    }

    /**
     * 修改地址簿
     * @param addressBook
     * @return
     */
    @Override
    public void update(AddressBook addressBook) {
        addressBookMapper.SetIsDefaultAddressBook(AddressBookConstant.ISDEFAULT);
        addressBook.setIsDefault(AddressBookConstant.DEFAULT);
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.update(addressBook);
    }

    /**
     * 删除地址簿
     * @param id
     * @return
     */
    @Override
    public void DelAddressBook(Long id) {
        AddressBook addressBook = addressBookMapper.GetAddressBookById(id);
        if (addressBook.getIsDefault() == AddressBookConstant.ISDEFAULT) {
            addressBookMapper.DelAddressBook(id);
            List<AddressBook> list = addressBookMapper.list(BaseContext.getCurrentId());
            list.get(0).getId();
            addressBookMapper.SetDefaultAddressBook(list.get(0).getId(), AddressBookConstant.DEFAULT);
        }
    }


}
