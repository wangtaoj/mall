package com.waston.dao;

import com.waston.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    /**
     * 根据订单号和用户ID查询所有订单明细
     * @param orderNo
     * @param userId
     * @return
     */
    List<OrderItem> selectListByOrderNoAndUserId(@Param("orderNo")Long orderNo, @Param("userId")Integer userId);

    /**
     * 根据订单号查询所有订单明细
     * @param orderNo
     * @return
     */
    List<OrderItem> selectListByOrderNo(Long orderNo);

    /**
     * 批量插入订单明细
     * @param orderItems
     * @return
     */
    int insertBatch(@Param("orderItems") List<OrderItem> orderItems);
}