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
}
