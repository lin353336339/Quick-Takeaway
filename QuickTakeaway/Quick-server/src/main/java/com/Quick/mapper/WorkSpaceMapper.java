package com.Quick.mapper;

import com.Quick.entity.AddressBook;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface WorkSpaceMapper {


    /**
     * 根据日期统计有效订单�?
     * @param map
     * @return
     */
    Integer getOrderCountByMap(Map map);

    /**
     * 根据日期统计用户数量
     * @param map
     * @return
     */
    Double getTurnoverByMap(Map map);
}
