package com.waston.service;

import com.waston.common.ServerResponse;

import java.util.Map;

/**
 * @Author wangtao
 * @Date 2018/1/22
 **/
public interface OrderService {

    /**
     * 支付接口
     * @param orderNo
     * @param userId
     * @param path
     * @return
     */
    ServerResponse pay(Long orderNo, Integer userId, String path);

    /**
     * 支付宝回调逻辑
     * @param params
     * @return
     */
    ServerResponse aliCallback(Map<String,String> params);

    /**
     * 查询订单支付状态
     * @param userId
     * @param orderNo
     * @return
     */
    ServerResponse queryOrderPayStatus(Integer userId,Long orderNo);

    /**
     * 创建订单
     * @param userId
     * @param shippingId
     * @return
     */
    ServerResponse addOrder(Integer userId, Integer shippingId);

    /**
     * 获取购物车里的商品信息
     * @param userId
     * @return
     */
    ServerResponse getOrderCartProduct(Integer userId);

    /**
     * 分页获取订单列表
     * @param pageNum
     * @param pageSize
     * @param userId
     * @return
     */
    ServerResponse listOrder(Integer pageNum, Integer pageSize, Integer userId);

    /**
     * 获取商品详情
     * @param orderNo
     * @param usreId
     * @return
     */
    ServerResponse getOrder(Long orderNo, Integer usreId);

    /**
     * 用户主动取消自己的订单并且恢复下单时减少的库存
     * @param orderNo
     * @param usreId
     * @return
     */
    ServerResponse updateOrderStatus(Long orderNo, Integer usreId);

    /**
     * 系统定时取消下单一小时还未支付的订单并且恢复下单时减少的库存
     * @param hour
     */
    void updateAndCloseOrder(int hour);

    /**
     * 订单发货
     * @param orderNo
     * @return
     */
    ServerResponse updateOrderStatusToSend(Long orderNo);

    /**
     * 管理员获取订单列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse listOrderByManage(int pageNum, int pageSize);

    /**
     * 管理员获取订单详情
     * @param orderNo
     * @return
     */
    ServerResponse getOrderByManage(Long orderNo);

    /**
     * 搜索订单信息
     * @param orderNo
     * @return
     */
    ServerResponse search(int pageNum, int pageSize, Long orderNo);
}
