package com.waston.dao;

import com.waston.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    /**
     * 根据主键ID和用户ID删除地址, 加一个ID是为了确保删除的是该用户的地址
     * @param id
     * @param userId
     * @return
     */
    int deleteByPrimaryKeyAndUserId(@Param("id")Integer id, @Param("userId") Integer userId);

    /**
     * 根据主键和用户ID联合修改地址, 同样是为了确保修改的是该用户的地址
     * @param record
     * @return
     */
    int updateByPrimaryKeyAndUserId(Shipping record);

    /**
     * 查询地址根据主键和用户id, 同样是为了确保是该用户的地址
     * @param id
     * @param userId
     * @return
     */
    Shipping selectByPrimaryKeyAndUserId(@Param("id")Integer id, @Param("userId") Integer userId);

    /**
     * 分页查询
     * @param userId
     * @return
     */
    List<Shipping> selectByPage(Integer userId);
}