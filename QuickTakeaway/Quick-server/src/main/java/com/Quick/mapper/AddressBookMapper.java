package com.Quick.mapper;

import com.Quick.entity.AddressBook;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressBookMapper {

    /**
     * 插入数据
     * @param addressBook
     */
    @Insert("insert into address_book (user_id, consignee, sex, phone, province_code, province_name, city_code, city_name, district_code, district_name, detail, label, is_default)"
            + "values (#{userId}, #{consignee}, #{sex}, #{phone}, #{provinceCode}, #{provinceName}, #{cityCode}, #{cityName}, #{districtCode}, #{districtName}, #{detail}, #{label}, #{isDefault})")
    void insert(AddressBook addressBook);

    /**
     * 查询地址
     * @param currentId
     * @return
     */
    @Select("select * from address_book where user_id = #{currentId}")
    List<AddressBook> list(Long currentId);

    @Select("select * from address_book where user_id = #{currentId} and is_default = #{isDefault}")
    AddressBook GetAddressBookByDefault(Long currentId, Integer isDefault);

    @Select("select * from address_book where id = #{id}")
    AddressBook GetAddressBookById(Long id);

    @Update("update address_book set is_default = #{isDefault} where id = #{id}")
    void SetDefaultAddressBook(Long id, Integer isDefault);

    @Update("update address_book set consignee = #{consignee}, sex = #{sex}, phone = #{phone}, province_code = #{provinceCode}, province_name = #{provinceName}, city_code = #{cityCode}, city_name = #{cityName}, district_code = #{districtCode}, district_name = #{districtName}, detail = #{detail}, label = #{label}, is_default = #{isDefault} where id = #{id}")
    void update(AddressBook addressBook);

    @Delete("delete from address_book where id = #{id}")
    void DelAddressBook(Long id);

    @Update("update address_book set is_default = #{isDefault}")
    void SetIsDefaultAddressBook(Integer isdefault);
}
