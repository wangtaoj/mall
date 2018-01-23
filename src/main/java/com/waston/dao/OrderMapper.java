package com.waston.dao;

import com.waston.pojo.Order;
import org.apache.ibatis.annotations.Param;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    /**
     * 根据订单号查询订单记录
     * @param orderNo
     * @return
     */
    Order selectByOrderNo(Long orderNo);

    /**
     * 根据主键以及用户ID查询订单信息
     * @param orderNo
     * @param userId
     * @return
     */
    Order selectByOrderNoAndUserId(@Param("orderNo")Long orderNo, @Param("userId")Integer userId);
}