package com.waston.service;

import com.waston.common.ServerResponse;
import com.waston.pojo.Shipping;

/**
 * @Author wangtao
 * @Date 2018/1/22
 **/
public interface ShippingService {

    /**
     * 添加或者更新地址
     * @param userId
     * @param shipping
     * @return
     */
    ServerResponse saveOrUpdateShipping(Integer userId, Shipping shipping);

    /**
     * 删除地址
     * @param shippingId
     * @param userId
     * @return
     */
    ServerResponse removeShipping(Integer shippingId, Integer userId);

    /**
     * 获取地址详情
     * @param shippingId
     * @param userId
     * @return
     */
    ServerResponse getShipping(Integer shippingId, Integer userId);

    /**
     * 分页获取地址列表
     * @param pageNum
     * @param pageSize
     * @param userId
     * @return
     */
    ServerResponse selectListShipping(int pageNum, int pageSize, Integer userId);
}
