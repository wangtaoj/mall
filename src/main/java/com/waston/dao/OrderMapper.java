package com.waston.dao;

import com.waston.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    /**
     * 根据用户ID查询所有订单信息
     * @param userId
     * @return
     */
    List<Order> selectByUserId(Integer userId);

    /**
     * 获取所有订单信息
     * @return
     */
    List<Order> selectAll();

    /**
     * 根据订单号进行模糊查询
     * @param orderNo
     * @return
     */
    List<Order> selectAllByOrderNo(String orderNo);

    /**
     * 关单操作查看需要关单的订单集合
     * @param status
     * @param time
     * @return
     */
    List<Order> selectNeedCloseOrder(@Param("status")Integer status, @Param("time")String time);
}